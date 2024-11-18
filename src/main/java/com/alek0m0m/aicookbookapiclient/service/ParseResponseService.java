package com.alek0m0m.aicookbookapiclient.service;

import com.alek0m0m.aicookbookapiclient.dto.RecipeDTO;
import com.alek0m0m.aicookbookapiclient.dto.RecipeDTOSimple;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ParseResponseService {
    private final ObjectMapper objectMapper;
    private static final List<List<RecipeDTOSimple>> logRecipes = new ArrayList<>();

    public ParseResponseService() {
        this.objectMapper = new ObjectMapper();
    }

    // ----------------- Main Operations -----------------
        // 1. parseRecipe
        // 1.1 parseGetRecipeJSON
        // 1.2 mapJsonToRecipeDTO



    public List<RecipeDTOSimple> parseRecipe(String content) {
        List<RecipeDTOSimple> recipes = new ArrayList<>();

        debugObject(content);

        try {
            // Step 1: Preprocess raw content (e.g., remove ```json tags)
            String cleanedContent = preprocessContent(content);
            debugObject(cleanedContent, "cleanedContent");

            // Improved truncation
            cleanedContent = truncateIncompleteJson(cleanedContent);
            debugObject(cleanedContent);
            
            // remove incomplete recipes


            // Step 2: Parse as JSON tree
            JsonNode rootNode = objectMapper.readTree(cleanedContent);
            debugObject(rootNode.toString());

            // Step 3: Validate and map recipes
            if (rootNode.isArray()) {
                ArrayNode recipesArray = (ArrayNode) rootNode;
                for (JsonNode recipeNode : recipesArray) {
                    debugObject(recipeNode.toString());
                    if (isCompleteRecipe(recipeNode)) {
                        RecipeDTOSimple recipe = objectMapper.treeToValue(recipeNode, RecipeDTOSimple.class);
                        debugObject(recipe.toString());
                        recipes.add(recipe);
                    }
                }
            } else {
                System.err.println("Error: Root JSON is not an array.");
            }

        } catch (Exception e) {
            System.err.println("Error parsing recipes: " + e.getMessage());
            e.printStackTrace();
        }

        return recipes;
    }

    private String removeIncompletes(String cleanedContent) {
        String START_OF_RECIPE = "{" + "\"name\"";
        // Stream the string and remove incomplete recipes

        List<String> strings = new ArrayList<>();
        Pattern pattern = Pattern.compile(START_OF_RECIPE); // compile = create a pattern
        ; // split the string based on the pattern, removing the pattern

        for (String s : pattern.split(cleanedContent)) {
            String s1 = s + START_OF_RECIPE;
            debugObject(s1);
            strings.add(s+START_OF_RECIPE);
        }
        return String.join("", strings);
    }

    public List<RecipeDTOSimple> parseRecipeStreaming(String content) {
        List<RecipeDTOSimple> recipes = new ArrayList<>();

        try {
            String cleanedContent = preprocessContent(content);

            // Create a JsonParser
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(cleanedContent);

            // Ensure the parser starts with START_ARRAY
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IOException("Expected data to start with an Array");
            }

            // Iterate over the array
            while (parser.nextToken() == JsonToken.START_OBJECT) {
                // Parse individual recipe
                JsonNode recipeNode = objectMapper.readTree(parser);
                if (isCompleteRecipe(recipeNode)) {
                    RecipeDTOSimple recipe = objectMapper.treeToValue(recipeNode, RecipeDTOSimple.class);
                    recipes.add(recipe);
                }
            }

        } catch (Exception e) {
            System.err.println("Error parsing recipes: " + e.getMessage());
            e.printStackTrace();
        }

        return recipes;
    }



    private String preprocessContent(String content) {
        content = content.trim();
        if (content.startsWith("```json")) {
            content = content.substring(7); // Remove ```json
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3); // Remove trailing ```
        }
        return content.trim();
    }

    private String truncateIncompleteJson(String json) {
        int openCurlyBraces = 0;
        int openSquareBrackets = 0;
        boolean inString = false;
        char prevChar = 0;

        String prevSeqOfSymbols = "";
        String seqOfSymbols = "";
        String currentRecipe = "";
        List<String> recipes = new ArrayList<>();
        boolean buildingRecipe = false;
        String START_OF_RECIPE = "{" + "\"name\"";
        String END_OF_RECIPE = "}]}";


        for (int i = 0; i < json.length(); i++) {
            char currentChar = json.charAt(i);

            // Handle string start/end
            if (currentChar == '"' && prevChar != '\\') {
                inString = !inString;
            }

            if (!inString) {
                if (currentChar == '{') {
                    openCurlyBraces++;
                } else if (currentChar == '}') {
                    openCurlyBraces--;
                } else if (currentChar == '[') {
                    openSquareBrackets++;
                } else if (currentChar == ']') {
                    openSquareBrackets--;
                }

                // Check if all braces and brackets are closed
                if (openCurlyBraces == 0 && openSquareBrackets == 0) {
                    return json.substring(0, i + 1);
                }

            }

            if (inString) {
                // sequenceOfSymbols done
                prevSeqOfSymbols = seqOfSymbols;
                seqOfSymbols = "";
            }

            if (prevSeqOfSymbols == START_OF_RECIPE) {
                buildingRecipe = true;
            }

            if (buildingRecipe) {
                currentRecipe += currentChar;
            }

            if (prevSeqOfSymbols == START_OF_RECIPE && seqOfSymbols == END_OF_RECIPE) {
                recipes.add(currentRecipe);
                currentRecipe = "";
            }

            prevChar = currentChar;
        }

        // Return the truncated JSON up to where it's balanced
        return recipes.toString();
    }


    private boolean isCompleteRecipe(JsonNode recipeNode) {
        return recipeNode.has("name") &&
                recipeNode.has("instructions") &&
                recipeNode.has("ingredients") &&
                recipeNode.get("ingredients").isArray();
    }

    int count = 0;

    private void debugObject(String ob, String name) {
        count++;
        System.out.println();
        System.out.println(" " + count + ". " + name + ": " + ob);
        System.out.println();
    }

    private void debugObject(String ob) {
        count++;
        System.out.println();
        System.out.println(" " + count + ". ob: " + ob);
        System.out.println();
    }

    private void mapObjects(String content, List<RecipeDTOSimple> recipes) {
        try {
            // Step 1: Preprocess raw content (e.g., remove ```json tags)
            String cleanedContent = preprocessContent(content);
            debugObject(cleanedContent);
            // Step 2: Parse as JSON tree
            JsonNode rootNode = objectMapper.readTree(cleanedContent);
            debugObject(rootNode.toString());

            // Step 3: Validate and map recipes
            if (rootNode.isArray()) {
                ArrayNode recipesArray = (ArrayNode) rootNode;
                for (JsonNode recipeNode : recipesArray) {
                    debugObject(recipeNode.toString());
                    if (isCompleteRecipe(recipeNode)) {
                        RecipeDTOSimple recipe = objectMapper.treeToValue(recipeNode, RecipeDTOSimple.class);
                        debugObject(recipe.toString());
                        recipes.add(recipe);
                    }
                }
            } else {
                System.err.println("Error: Root JSON is not an array.");
            }

        } catch (Exception e) {
            System.err.println("Error parsing recipes: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public String getExampleJSONContent() {
        return "```json"
                + "["
                + "{"
                // "name": "Simple Cucumber Pasta Salad",
                + "\"name\": \"Simple Cucumber Pasta Salad\","
                // "instructions": "Cook the pasta in boiling salted water until al dente. Rinse under cold water and drain. Chop the cucumber into bite-size pieces. In a large bowl, mix the cooked pasta with chopped cucumber. Add olive oil, lemon juice, and season with salt and pepper to taste. Toss everything together and serve chilled.",
                + "\"instructions\": \"Cook the pasta in boiling salted water until al dente. Rinse under cold water and drain. Chop the cucumber into bite-size pieces. In a large bowl, mix the cooked pasta with chopped cucumber. Add olive oil, lemon juice, and season with salt and pepper to taste. Toss everything together and serve chilled.\","
                // "ingredients": [
                + "\"ingredients\": ["
                // {
                + "{"
                // "id": "1",
                + "\"id\": \"1\","
                // "name": "Pasta",
                + "\"name\": \"Pasta\","
                // "amount": 100,
                + "\"amount\": 100,"
                // "unit": "g"
                + "\"unit\": \"g\""
                + "},"
                // {
                + "{"
                // "id": "2",
                + "\"id\": \"2\","
                // "name": "Salt",
                + "\"name\": \"Salt\","
                // "amount": 5,
                + "\"amount\": 5,"
                // "unit": "g"
                + "\"unit\": \"g\""
                + "},"
                // {
                + "{"
                // "id": "3",
                + "\"id\": \"3\","
                // "name": "Cucumber",
                + "\"name\": \"Cucumber\","
                // "amount": 150,
                + "\"amount\": 150,"
                // "unit": "g"
                + "\"unit\": \"g\""
                + "},"
                // {
                + "{"
                // "id": "4",
                + "\"id\": \"4\","
                // "name": "Olive oil",
                + "\"name\": \"Olive oil\","
                // "amount": 20,
                + "\"amount\": 20,"
                // "unit": "ml"
                + "\"unit\": \"ml\""
                + "},"
                // {
                + "{"
                // "id": "5",
                + "\"id\": \"5\","
                // "name": "Lemon juice",
                + "\"name\": \"Lemon juice\","
                // "amount": 10,
                + "\"amount\": 10,"
                // "unit": "ml"
                + "\"unit\": \"ml\""
                + "}"
                // ]
                + "]"
                + "},"
                + "{"
                // "name": "Chilled Cucumber Noodle Bowl",
                + "\"name\": \"Chilled Cucumber Noodle Bowl\","
                // "instructions": "Peel and thinly slice the cucumber into noodle-like strips using a vegetable peeler or spiralizer. Cook the pasta in boiling salted water until al dente, then rinse under cold water and drain. Combine the cucumber noodles and pasta in a bowl. Drizzle with soy sauce, sesame oil, and a bit of lime juice. Toss gently and serve immediately.",
                + "\"instructions\": \"Peel and thinly slice the cucumber into noodle-like strips using a vegetable peeler or spiralizer. Cook the pasta in boiling salted water until al dente, then rinse under cold water and drain. Combine the cucumber noodles and pasta in a bowl. Drizzle with soy sauce, sesame oil, and a bit of lime juice. Toss gently and serve immediately.\","
                // "ingredients": [
                + "\"ingredients\": ["

                + "{"
                // "id": "6",
                + "\"id\": \"6\","
                + "\"name\": \"Cucumber\","
                + "\"amount\": 200,"
                + "\"unit\": \"g\""
                + "},"
                + "{"
                + "\"id\": \"7\","
                + "\"name\": \"Pasta\","
                + "\"amount\": 100,"
                + "\"unit\": \"g\""
                + "},"
                + "{"
                + "\"id\": \"8\","
                + "\"name\": \"Salt\","
                + "\"amount\": 5,"
                + "\"unit\": \"g\""
                + "},"
                + "{"
                + "\"id\": \"9\","
                + "\"name\": \"Soy sauce\","
                + "\"amount\": 15,"
                + "\"unit\": \"ml\""
                + "},"
                + "{"
                + "\"id\"";
    };


    // ----------------- Unused Operations -----------------

    public List<RecipeDTOSimple> parseRecipe2(String content) {
        System.out.println("Start Parsing");
        List<RecipeDTOSimple> recipes = new ArrayList<>();

        String processedContent = preprocessContent(content);

        String[] recipesArr = filterContentForRecipes(processedContent);

        for (String recipeStr : recipesArr) {
            recipes.add(mapContentToDTO(recipeStr));
        }

        debugPrint(content, processedContent, recipes);
        logRecipes.add(recipes);
        return recipes;
    }

    private String[] filterContentForRecipes(String content) {
        // [{"name": "Simple Cucumber Pasta Salad","instructions": "Cook the pasta in boiling salted water until al dente. Rinse under cold water and drain. Chop the cucumber into bite-size pieces. In a large bowl, mix the cooked pasta with chopped cucumber. Add olive oil, lemon juice, and season with salt and pepper to taste. Toss everything together and serve chilled.","ingredients": [{"id": "1","name": "Pasta","amount": 100,"unit": "g"},{"id": "2","name": "Salt","amount": 5,"unit": "g"},{"id": "3","name": "Cucumber","amount": 150,"unit": "g"},{"id": "4","name": "Olive oil","amount": 20,"unit": "ml"},{"id": "5","name": "Lemon juice","amount": 10,"unit": "ml"}]},{"name": "Chilled Cucumber Noodle Bowl","instructions": "Peel and thinly slice the cucumber into noodle-like strips using a vegetable peeler or spiralizer. Cook the pasta in boiling salted water until al dente, then rinse under cold water and drain. Combine the cucumber noodles and pasta in a bowl. Drizzle with soy sauce, sesame oil, and a bit of lime juice. Toss gently and serve immediately.","ingredients": [{"id": "6","name": "Cucumber","amount": 200,"unit": "g"},{"id": "7","name": "Pasta","amount": 100,"unit": "g"},{"id": "8","name": "Salt","amount": 5,"unit": "g"},{"id": "9","name": "Soy sauce","amount": 15,"unit": "ml"},{"
        // above is content. need to adjust so that readTree can read it.

        System.out.println(" content: " + content.substring(0, content.length() > 5 ? 5 : content.length()));

        // start of recipe: '{"name"'
        String START_OF_RECIPE = "{" + "\"name\"";
        String END_OF_RECIPE = "}]}";

        String[] recipes = content.split(END_OF_RECIPE);



        StringBuilder filteredContent = new StringBuilder();

        System.out.println(" recipes: " );
        System.out.println(" 1. recipe:"+ recipes[0].substring(0, recipes[0].length() > 10 ? 10 : recipes[0].length()));
        System.out.println(" 2. recipe:"+ recipes[1].substring(0, recipes[1].length() > 10 ? 10 : recipes[1].length()));

        String filteredToRecipes[] = new String[recipes.length];
        for (int i = 0; i < recipes.length; i++) {
           filteredToRecipes[i] = START_OF_RECIPE + recipes[i];
        }

        System.out.println(" recipes: " );
        System.out.println(" 1. recipe:"+ recipes[0].substring(0, recipes[0].length() > 10 ? 10 : recipes[0].length()));
        System.out.println(" 2. recipe:"+ recipes[1].substring(0, recipes[1].length() > 10 ? 10 : recipes[1].length()));

        return filteredToRecipes;
    }


    private void debugPrint(String content, String processedContent, List<RecipeDTOSimple> recipes) {
        System.out.println(" RecipeDTOSimples: " + recipes);
        System.out.println("END Parsing");
        System.out.println();
    }

    public List<RecipeDTOSimple> getLatestRecipes() {
        return logRecipes.get(logRecipes.size() - 1);
    }



    // ----------------- Unused Operations -----------------

    private RecipeDTOSimple mapContentToDTO(String content) {
        RecipeDTOSimple recipe = new RecipeDTOSimple();
        mapJsonToRecipeDTO(parseGetRecipeJSON(content), recipe);
        return recipe;
    }

    private static void mapJsonToRecipeDTO(String json, RecipeDTOSimple recipeDTO) {
        String[] parts = json.split(",");

        for (String part : parts) {
//            if (part.contains("id")) {
//                recipeDTO.setId(Long.parseLong(part.split(":")[1]));
//            } else
                if (part.contains("name")) {
                recipeDTO.setName(part.split(":")[1]);
            } else if (part.contains("instructions")) {
                recipeDTO.setInstructions(part.split(":")[1]);
            }
        }
    }
    private static void mapJsonToRecipeDTO(String json, RecipeDTO recipeDTO) {
        String[] parts = json.split(",");

        for (String part : parts) {
            if (part.contains("id")) {
                recipeDTO.setId(Long.parseLong(part.split(":")[1]));
            } else if (part.contains("name")) {
                recipeDTO.setName(part.split(":")[1]);
            } else if (part.contains("instructions")) {
                recipeDTO.setInstructions(part.split(":")[1]);
            } else if (part.contains("tags")) {
                recipeDTO.setTags(part.split(":")[1]);
            } else if (part.contains("servings")) {
                recipeDTO.setServings(Integer.parseInt(part.split(":")[1]));
            } else if (part.contains("prepTime")) {
                recipeDTO.setPrepTime(Integer.parseInt(part.split(":")[1]));
            } else if (part.contains("cookTime")) {
                recipeDTO.setCookTime(Integer.parseInt(part.split(":")[1]));
            } else if (part.contains("totalTime")) {
                recipeDTO.setTotalTime(Integer.parseInt(part.split(":")[1]));
            }
        }
    }

    private String parseGetRecipeJSON(String content) {
        StringBuilder json = new StringBuilder();

        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '{') {
                json.append(content.charAt(i));
                i++;
                while (content.charAt(i) != '}') {
                    json.append(content.charAt(i));
                    i++;
                }
                json.append(content.charAt(i));
                break;
            }
        }

        return json.toString();

    }


    public List<RecipeDTOSimple> parseRecipeTest() {
        return parseRecipe(getExampleJSONContent());
    }


    /* Content printout:
     content: ```json
[
    {
        "name": "Simple Cucumber Pasta Salad",
        "instructions": "Cook the pasta in boiling salted water until al dente. Rinse under cold water and drain. Chop the cucumber into bite-size pieces. In a large bowl, mix the cooked pasta with chopped cucumber. Add olive oil, lemon juice, and season with salt and pepper to taste. Toss everything together and serve chilled.",
        "ingredients": [
            {
                "id": "1",
                "name": "Pasta",
                "amount": 100,
                "unit": "g"
            },
            {
                "id": "2",
                "name": "Salt",
                "amount": 5,
                "unit": "g"
            },
            {
                "id": "3",
                "name": "Cucumber",
                "amount": 150,
                "unit": "g"
            },
            {
                "id": "4",
                "name": "Olive oil",
                "amount": 20,
                "unit": "ml"
            },
            {
                "id": "5",
                "name": "Lemon juice",
                "amount": 10,
                "unit": "ml"
            }
        ]
    },
    {
        "name": "Chilled Cucumber Noodle Bowl",
        "instructions": "Peel and thinly slice the cucumber into noodle-like strips using a vegetable peeler or spiralizer. Cook the pasta in boiling salted water until al dente, then rinse under cold water and drain. Combine the cucumber noodles and pasta in a bowl. Drizzle with soy sauce, sesame oil, and a bit of lime juice. Toss gently and serve immediately.",
        "ingredients": [
            {
                "id": "6",
                "name": "Cucumber",
                "amount": 200,
                "unit": "g"
            },
            {
                "id": "7",
                "name": "Pasta",
                "amount": 100,
                "unit": "g"
            },
            {
                "id": "8",
                "name": "Salt",
                "amount": 5,
                "unit": "g"
            },
            {
                "id": "9",
                "name": "Soy sauce",
                "amount": 15,
                "unit": "ml"
            },
            {
                "id

     */ // in the above, we need to start rootNode properly and then create the Recipe one at a time, since the second Recipe didnt get finished, then only the first recipe should be passed on.

}
