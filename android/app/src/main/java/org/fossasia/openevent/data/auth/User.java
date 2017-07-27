package org.fossasia.openevent.data.auth;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Type("user")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "email"})
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class User {

    @Id(IntegerIdHandler.class)
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isAdmin;
    private boolean isSuperAdmin;
    private String createdAt;
    private String lastAccessedAt;
    private String contact;
    private String deletedAt;
    private String details;
    private boolean isVerified;
    private String thumbnailImageUrl;
    private String iconImageUrl;
    private String smallImageUrl;
    private String avatarUrl;
    private String facebookUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String googlePlusUrl;
    private String originalImageUrl;

    public User(String email) {
        this.email = email;
    }
}
