package org.fossasia.openevent.common.arch;

import android.arch.lifecycle.LiveData;

import io.realm.RealmChangeListener;
import io.realm.RealmObject;

public class LiveRealmDataObject<T extends RealmObject> extends LiveData<T> {
    private T data;
    private final RealmChangeListener<T> listener =
            this::setValue;

    public LiveRealmDataObject(T data) {
        this.data = data;
    }

    @Override
    protected void onActive() {
        data.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        data.removeChangeListener(listener);
    }
}
