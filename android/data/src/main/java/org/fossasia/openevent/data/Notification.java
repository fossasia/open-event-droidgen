package org.fossasia.openevent.data;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Type("notification")
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class Notification extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    private String title;
    private String message;
    private boolean isRead;
    private String receivedAt;
}
