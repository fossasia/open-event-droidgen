package org.fossasia.openevent;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.Version;
import org.junit.BeforeClass;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by MananWason on 7/31/2015.
 */
public class DAL {
    private static DAL dal = new DAL();

//    public List<Track> getAllTracks(){
//        return Collections.EMPTY_LIST;
//    }
//
//    public Track getTrack(String isbn){
//        return null;
//    }
//
//    public int addTrack(Track track){
//        return track.getId();
//    }
//
//    public int updateTrack(Track track){
//        return track.getId();
//    }
//
//    public static DAL getInstance(){
//        return dal;
//    }

    public List<Session> getSessionList() throws ParseException {
        return Collections.EMPTY_LIST;

    }

//    public Event getEventDetails() {
//        return databaseOperations.getEventDetails(mDb);
//    }
//
//    public Session getSessionById(int id) throws ParseException {
//        getReadOnlyDatabase();
//        return databaseOperations.getSessionById(id, mDb);
//    }

    public List<Speaker> getSpeakerList() {
        return Collections.EMPTY_LIST;

    }

//    public Speaker getSpeakerById(int id) {
//        getReadOnlyDatabase();
//        return databaseOperations.getSpeakerById(id, mDb);
//    }

    public List<Track> getTrackList() {
        return Collections.EMPTY_LIST;
    }


    public List<Sponsor> getSponsorList() {
        return Collections.EMPTY_LIST;
    }


//    public ArrayList<Session> getSessionbyTracksname(String trackName) throws ParseException {
//        return databaseOperations.getSessionbyTracksname(trackName, mDb);
//    }
//
//    public ArrayList<Session> getSessionbySpeakersName(String speakerName) throws ParseException {
//        return databaseOperations.getSessionbySpeakersname(speakerName, mDb);
//    }
//
//    public ArrayList<Speaker> getSpeakersbySessionName(String sessionName) throws ParseException {
//        return databaseOperations.getSpeakersbySessionname(sessionName, mDb);
//    }
//
//
//    public Track getTrackbyName(String trackName) throws ParseException {
//        return databaseOperations.getTracksbyTracksname(trackName, mDb);
//    }
//
//    public Speaker getSpeakerbySpeakersname(String speakerName) throws ParseException {
//        return databaseOperations.getSpeakerbySpeakersname(speakerName, mDb);
//    }
//
//    public Session getSessionbySessionname(String sessionName) throws ParseException {
//        return databaseOperations.getSessionbySessionname(sessionName, mDb);
//    }

    public List<Integer> getBookmarkIds() throws ParseException {
        return Collections.EMPTY_LIST;
    }
}
