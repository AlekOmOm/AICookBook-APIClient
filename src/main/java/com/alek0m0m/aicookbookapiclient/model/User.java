package com.alek0m0m.aicookbookapiclient.model;

// import from library AlekOmOm
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Entity
@Component
@Getter
@Setter
public class User {

    private Long id;
    private String username;
    private String password;


}