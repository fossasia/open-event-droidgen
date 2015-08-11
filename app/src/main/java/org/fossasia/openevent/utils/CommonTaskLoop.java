package org.fossasia.openevent.utils;

import android.util.Log;

/**
 * Created by MananWason on 8/3/2015.
 */
public class CommonTaskLoop {
    private static CommonTaskLoop ourInstance = new CommonTaskLoop();

    private CommonEventLoop m_loop;

    private CommonTaskLoop() {
        m_loop = new CommonEventLoop();
    }

    public static CommonTaskLoop getInstance() {
        Log.d("TASKLOOP", "Get instance");
        return ourInstance;
    }

    public void post(Runnable call) {
        Log.d("TASKLOOP", "CALL POSTED");
        m_loop.post(call);
    }

    public void delayPost(Runnable call, int nMillSec) {
        m_loop.delayPost(call, nMillSec);
    }

    public void shutdown() {
        m_loop.shutdown();
    }
}
