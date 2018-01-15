package org.fossasia.openevent.dbutils;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Predicate;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * A wrapper around LiveData
 * Allows filtering of the LiveData components using ReactiveX predicate.
 * @param <V>: The data type of the List.
 */
public class FilterableRealmLiveData<V extends RealmModel> extends LiveData<List<V>> {

    private RealmResults<V> unfilteredData;
    private final CompositeDisposable compositeDisposable;
    private Predicate<V> predicate;
    private final RealmChangeListener<RealmResults<V>> listener = result -> filter(predicate);

    public FilterableRealmLiveData(RealmResults<V> unfilteredData) {
        this.unfilteredData = unfilteredData;
        compositeDisposable = new CompositeDisposable();
    }

    public void filter(Predicate<V> predicate) {
        if (predicate != null) {
            this.predicate = predicate;
            compositeDisposable.add(Observable.fromIterable(unfilteredData).filter(predicate).toList()
                    .subscribe( filteredList -> {
                        setValue(filteredList);
                        Timber.d("Filtering done total results %d", filteredList.size());
                        if (filteredList.isEmpty()) {
                            Timber.e("No results published. There is an error in query. Check " + getClass().getName() + " filter!");
                        }
                    }));
        } else {
            setValue(unfilteredData);
        }
    }

    @Override
    protected void onActive() {
        unfilteredData.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        unfilteredData.removeChangeListener(listener);
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
