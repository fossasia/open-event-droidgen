package org.fossasia.openevent.events;

/**
 * Created by Manan Wason on 25/06/16.
 */
public class RetrofitError {
    private Throwable throwable;

    public RetrofitError(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
