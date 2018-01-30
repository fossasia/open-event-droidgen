package org.fossasia.openevent.common.events;

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
