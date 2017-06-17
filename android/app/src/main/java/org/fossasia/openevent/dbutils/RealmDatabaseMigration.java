package org.fossasia.openevent.dbutils;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

public class RealmDatabaseMigration implements RealmMigration {

    public static final long DB_VERSION = 0;

    /**
     * Defines migration strategy of Realm Database
     * Refer to https://realm.io/docs/java/latest/#migrations for more info
     * @param dynamicRealm realm schema instance
     * @param oldVersion The existing database version
     * @param newVersion The database version migration will happen to
     */
    @Override
    public void migrate(DynamicRealm dynamicRealm, long oldVersion, long newVersion) {

        // Add schema increments and change methods when updating models

        /* Example migration :

        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();

        // Migrate to version 1: Add a new class.
        if (oldVersion == 0) {
            schema.create("Person")
                .addField("name", String.class)
                .addField("age", int.class);
            oldVersion++;
         }

         // Migrate to version 2: Add a primary key + object references
         if (oldVersion == 1) {
            schema.get("Person")
                .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                .addRealmObjectField("favoriteDog", schema.get("Dog"))
                .addRealmListField("dogs", schema.get("Dog"));
            oldVersion++;
         }

         */

    }

}
