
# AICookBook

API Client for AICookBook-Application

### Setup and Run the Application

#### Repos .git links

Three services
- Frontend:
	- [GitHub - AlekOmOm/AICookBook-Frontend: Frontend for AICookBook-Application](https://github.com/AlekOmOm/AICookBook-Frontend.git)
- APIClient:
	- [GitHub - AlekOmOm/AICookBook-APIClient](https://github.com/AlekOmOm/AICookBook-APIClient.git)
- Backend:
	- [GitHub - AlekOmOm/AICookBook-Backend](https://github.com/AlekOmOm/AICookBook-Backend.git)

#### Steps to Run the Application:

1. Backend
	- Port = 8080
	  steps:
	1. DB create Schema:
		- aicookbook_db
	2. DB Env variables
		- DB_LOCALHOST = localhost
		- DB_PORT = 3306
		- DB_USERNAME = < your_db_username >
		- DB_PASSWORD = < your_db_password >

2. Frontend
	1. run with:
		- `npm install`
		- `npm run dev`

3. APIClient:
	- Port = 8081
	1. Env variables
		1. Create .env file
		   add in .env:  `API_KEY= <your-key>`



  
### Project Overview: AI-Enhanced Cookbook Application

**Objective:**
- To create a proof-of-concept system that integrates AI-driven functionalities to enhance a cookbook application. 
- The project emphasizes user-customized recipe recommendations and streamlined shopping list generation based on available ingredients and user preferences.

**Core Functionality:**
1. **Ingredients Management**: 
	- Users log in, upload, or update their list of available ingredients.
2. **'Choose' - Recipe Suggestions**: 
   - The system suggests recipes using AI recommendations based on:
	   - user profile preferences 
	   - and available ingredients.
   - Recipes Categorization:
	   - “Best Use” (recipes using at least 50% of available ingredients) 
	   - “Good Alternatives” (recipes using at least 25%).
3. **'Shop' - Shopping List Generation**: 
	- Once a recipe is chosen, the system generates a shopping list for missing ingredients.
4. **'Cook' - Retrieve Recipe Instructions**
	- precondition: 
		- recipe chose + shopping done
		- Instructions included in the initial Generation of Recipe, which is temp-saved  
	-  System retrieves Recipe Instruction for easy Cooking of recipe. 
			


**User Workflow:**
1. **Login**: Access the application.
2. **Setup Ingredients**: Users add or update their ingredient list.
3. **Intent Selection**: Users choose an intent such as "Explore" (new recipes) or "Use Known" (based on familiar ingredients), and set or customize preferences.
4. **Recipe Exploration**: Based on filters (e.g., vegetarian, work time), users receive tailored recipe recommendations.
5. **Recipe Details and Shopping List**: Upon recipe selection, detailed instructions and an auto-generated shopping list are provided.






## Getting Started

  

### Clone the Repository

Clone the repository into the root of your folder:

```
	git clone https://github.com/AlekOmOm/AICookBook.git .
```

set new remote

```
	git remote remove origin
	git remote add origin <new repo .git>
```

install dependencies and run

```
	npm install
	npm run dev
```
