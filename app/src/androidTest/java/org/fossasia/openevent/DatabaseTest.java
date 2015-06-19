package org.fossasia.openevent;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;
import org.fossasia.openevent.dbutils.DbSingleton;

/**
 * Created by MananWason on 17-06-2015.
 */
public class DatabaseTest extends AndroidTestCase {
    public void TestDropDB() {
        assertTrue(mContext.deleteDatabase(DbContract.DATABASE_NAME));
    }

    public void testCreateDB() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());
        db.close();
    }

    public void testSpeakersList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        assertNotNull(dbSingleton.getSpeakerList());
        assertTrue(dbSingleton.getSpeakerList().size() > 0);
    }

    public void testSessionsList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        assertNotNull(dbSingleton.getSessionList());
        assertTrue(dbSingleton.getSessionList().size() > 0);
    }

    public void testTracksList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        assertNotNull(dbSingleton.getTrackList());
        assertTrue(dbSingleton.getTrackList().size() > 0);
    }

    public void testVersionList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        assertNotNull(dbSingleton.getVersionIds());
    }

    public void testSponsorsList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        assertNotNull(dbSingleton.getSponsorList());
        assertTrue(dbSingleton.getSponsorList().size() > 0);
    }

}