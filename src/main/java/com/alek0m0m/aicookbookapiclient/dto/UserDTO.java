package com.alek0m0m.aicookbookapiclient.dto;

import com.alek0m0m.aicookbookapiclient.model.User;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    private Long id;
    private String username;
    private String password;


    public User toEntity() {
        User user = new User();
        user.setId(getId());
        user.setUsername(getUsername());
        user.setPassword(getPassword());
        return user;
    }


}
