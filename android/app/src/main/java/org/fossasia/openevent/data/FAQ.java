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

/**
 * Created by mayank on 4/2/18.
 */
@Data
@Type("faq")
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class FAQ extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;

    private String question;
    private String answer;

//    @Relationship("faq-type")
//    private RealmList<FAQType> faqTypes;
}
