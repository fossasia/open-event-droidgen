package org.fossasia.openevent.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;

import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.dbutils.DbSingleton;

/**
 * Created by MananWason on 05-06-2015.
 */
public class SponsorsFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DbSingleton dbSingleton = DbSingleton.getInstance();
    }
}
