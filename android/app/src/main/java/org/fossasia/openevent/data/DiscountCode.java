package org.fossasia.openevent.data;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Type("discount-code")
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class DiscountCode extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    private String code;
    private String discountUrl;
    private float value;
    private String type;
    private Boolean isActive;
    private int ticketsNumber;
    private int minQuantity;
    private int maxQuantity;
    private String tickets;
    private String usedFor;

}