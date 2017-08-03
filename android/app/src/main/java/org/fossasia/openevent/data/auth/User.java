package org.fossasia.openevent.data.auth;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Type("user")
@Builder()
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "email"}, callSuper = true)
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class User extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    @Ignore
    private String password;
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
}
