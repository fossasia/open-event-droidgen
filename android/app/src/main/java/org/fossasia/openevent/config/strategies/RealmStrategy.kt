package org.fossasia.openevent.config.strategies

import android.content.Context

import org.fossasia.openevent.config.ConfigStrategy
import org.fossasia.openevent.data.module.DataModule
import org.fossasia.openevent.data.repository.RealmDatabaseMigration

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Configures Realm Database
 */
class RealmStrategy : ConfigStrategy {

    override fun configure(context: Context): Boolean {
        Realm.init(context)
        val config = RealmConfiguration.Builder()
                .schemaVersion(RealmDatabaseMigration.DB_VERSION) // Must be bumped when the schema changes
                .modules(DataModule())
                //TODO: Re-add migration once DB is locked/finalized
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(config)

        return false
    }

}
