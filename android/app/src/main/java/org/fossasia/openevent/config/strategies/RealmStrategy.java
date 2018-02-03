package org.fossasia.openevent.config.strategies;

import android.content.Context;

import org.fossasia.openevent.config.ConfigStrategy;
import org.fossasia.openevent.data.repository.RealmDatabaseMigration;

import io.realm.Realm;

/**
 * Configures Realm Database
 */
public class RealmStrategy implements ConfigStrategy {

    @Override
    public boolean configure(Context context) {
        Realm.init(context);
        io.realm.RealmConfiguration config = new io.realm.RealmConfiguration.Builder()
                .schemaVersion(RealmDatabaseMigration.DB_VERSION) // Must be bumped when the schema changes
                //TODO: Re-add migration once DB is locked/finalized
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);

        return false;
    }

}
