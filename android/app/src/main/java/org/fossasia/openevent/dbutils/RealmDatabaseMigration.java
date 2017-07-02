package org.fossasia.openevent.dbutils;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class RealmDatabaseMigration implements RealmMigration {

    public static long DB_VERSION = 1;

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();
        if(oldVersion == 0) {
            schema.get("Session")
                    .addField("created-at", String.class)
                    .addField("deleted-at", String.class)
                    .addField("submitted-at", String.class)
                    .addField("is-mail-sent", Boolean.class);
            oldVersion++;
        }
    }
}
