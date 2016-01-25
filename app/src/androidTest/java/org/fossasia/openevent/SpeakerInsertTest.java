package org.fossasia.openevent;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;
import android.util.Log;

import com.google.gson.Gson;

import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.helper.IOUtils;

import java.util.List;

/**
 * Created by MananWason on 17-06-2015.
 */
public class SpeakerInsertTest extends AndroidTestCase {
    private static final String TAG = SpeakerInsertTest.class.getSimpleName();

    private static int id;

    private static String name;

    private static String photo;

    private static String bio;

    private static String email;

    private static String web;

    private static String twitter;

    private static String facebook;

    private static String github;

    private static String linkedin;

    private static String organisation;

    private static String position;

    private static int[] session;

    private static String country;

    private static long speakerAssignId;

    private DbHelper db;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        db = new DbHelper(mContext);
        SQLiteDatabase database = db.getWritableDatabase();

        id = 1;
        name = "Manan";
        photo = "https://media.licdn.com/mpr/mpr/shrinknp_400_400/p/8/005/05f/03a/3f2a7fd.jpg";
        bio = "event android app";
        email = "manan.wason@gmail.com";
        web = "mananwason.wordpress.com";
        twitter = "twitter.com/mananwason9";
        facebook = "facebook.com/manan";
        github = "github.com/mananwason";
        linkedin = "linkedin.com/mananwason";
        organisation = "fossasia";
        position = null;
        session = null;
        country = "india";

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.Speakers.NAME, name);
        contentValues.put(DbContract.Speakers.PHOTO, photo);
        contentValues.put(DbContract.Speakers.BIO, bio);
        contentValues.put(DbContract.Speakers.EMAIL, email);
        contentValues.put(DbContract.Speakers.WEB, web);
        contentValues.put(DbContract.Speakers.TWITTER, twitter);
        contentValues.put(DbContract.Speakers.FACEBOOK, facebook);
        contentValues.put(DbContract.Speakers.GITHUB, github);
        contentValues.put(DbContract.Speakers.LINKEDIN, linkedin);
        contentValues.put(DbContract.Speakers.ORGANISATION, organisation);
        contentValues.put(DbContract.Speakers.POSITION, position);
        contentValues.put(DbContract.Speakers.COUNTRY, country);

        speakerAssignId = database.insert(DbContract.Speakers.TABLE_NAME, null, contentValues);
        assertTrue(speakerAssignId != -1);
    }

    public void testDataCorrectness() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DbContract.Speakers.TABLE_NAME, null, null, null, null, null, null);
        assertTrue(cursor.moveToFirst());

        int idColumnIndex = cursor.getColumnIndex(DbContract.Speakers.ID);
        int dbId = cursor.getInt(idColumnIndex);

        int nameColumnIndex = cursor.getColumnIndex(DbContract.Speakers.NAME);
        String dbName = cursor.getString(nameColumnIndex);

        int photoColumnIndex = cursor.getColumnIndex(DbContract.Speakers.PHOTO);
        String dbPhoto = cursor.getString(photoColumnIndex);

        int bioColumnIndex = cursor.getColumnIndex(DbContract.Speakers.BIO);
        String dbBio = cursor.getString(bioColumnIndex);

        int emailColumnIndex = cursor.getColumnIndex(DbContract.Speakers.EMAIL);
        String dbEmail = cursor.getString(emailColumnIndex);

        int webColumnIndex = cursor.getColumnIndex(DbContract.Speakers.WEB);
        String dbWeb = cursor.getString(webColumnIndex);

        int twitterColumnIndex = cursor.getColumnIndex(DbContract.Speakers.TWITTER);
        String dbTwitter = cursor.getString(twitterColumnIndex);

        int facebookColumnIndex = cursor.getColumnIndex(DbContract.Speakers.FACEBOOK);
        String dbFacebook = cursor.getString(facebookColumnIndex);

        int githubColumnIndex = cursor.getColumnIndex(DbContract.Speakers.GITHUB);
        String dbGithub = cursor.getString(githubColumnIndex);

        int linkedinColumnIndex = cursor.getColumnIndex(DbContract.Speakers.LINKEDIN);
        String dbLinkedin = cursor.getString(linkedinColumnIndex);

        int organisationColumnIndex = cursor.getColumnIndex(DbContract.Speakers.ORGANISATION);
        String dbOrganisation = cursor.getString(organisationColumnIndex);

        int positionColumnIndex = cursor.getColumnIndex(DbContract.Speakers.POSITION);
        String dbPosition = cursor.getString(positionColumnIndex);

        int countryColumnIndex = cursor.getColumnIndex(DbContract.Speakers.COUNTRY);
        String dbCountry = cursor.getString(countryColumnIndex);
        cursor.close();
        assertEquals(id, dbId);
        assertEquals(name, dbName);
        assertEquals(photo, dbPhoto);
        assertEquals(bio, dbBio);
        assertEquals(email, dbEmail);
        assertEquals(web, dbWeb);
        assertEquals(twitter, dbTwitter);
        assertEquals(facebook, dbFacebook);
        assertEquals(github, dbGithub);
        assertEquals(linkedin, dbLinkedin);
        assertEquals(organisation, dbOrganisation);
        assertEquals(position, dbPosition);
        assertEquals(country, dbCountry);
    }


    /**
     * Checks that null values are correctly coerced into empty strings
     */
    public void testDataInsertionIsCorrect() {
        Gson gson = new Gson();
        String jsonStr = IOUtils.readRaw(org.fossasia.openevent.test.R.raw.speaker_v1, InstrumentationRegistry.getContext());
        Log.d(TAG, jsonStr);
        Speaker speaker = gson.fromJson(jsonStr, Speaker.class);
        String query = speaker.generateSql();
        DbSingleton.init(InstrumentationRegistry.getContext());
        DbSingleton instance = DbSingleton.getInstance();
        instance.clearDatabase(DbContract.Speakers.TABLE_NAME);
        instance.insertQuery(query);

        List<Speaker> speakerList = instance.getSpeakerList();
        assertTrue(speakerList != null);
        assertTrue(speakerList.size() == 1);
        Speaker speaker2 = speakerList.get(0);
        // NULL String must be transformed into an empty string upon insertion
        assertEquals(null, speaker.getPosition());

        // Must be empty string
        assertEquals("", speaker2.getPosition());

        // NULL must be converted to empty string
        assertEquals("", speaker2.getGithub());
    }

    @Override
    protected void tearDown() throws Exception {
        getContext().deleteDatabase(DbContract.DATABASE_NAME);
        db.close();
        super.tearDown();
    }
}
