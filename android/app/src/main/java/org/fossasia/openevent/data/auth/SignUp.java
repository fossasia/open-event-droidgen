package org.fossasia.openevent.data.auth;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Type("user")
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class SignUp {

    @Id
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String details;
    private String contact;
    private String avatarUrl;
    private String facebookUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String googlePlusUrl;
    private String originalImageUrl;

    public SignUp(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
