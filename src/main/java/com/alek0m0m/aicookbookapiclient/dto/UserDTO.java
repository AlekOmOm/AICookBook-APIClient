package com.alek0m0m.aicookbookapiclient.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.*;
import lombok.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "username",
        "password"
})
public class UserDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}