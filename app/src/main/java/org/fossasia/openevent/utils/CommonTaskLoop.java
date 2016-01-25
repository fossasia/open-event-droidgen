package org.fossasia.openevent.utils;

/**
 * User: MananWason
 * Date: 8/3/2015
 */
public class CommonTaskLoop {
    private static CommonTaskLoop ourInstance = new CommonTaskLoop();

    private CommonEventLoop commonEventLoop;

    private CommonTaskLoop() {
        commonEventLoop = new CommonEventLoop();
    }

    public static CommonTaskLoop getInstance() {
        return ourInstance;
    }

    public void post(Runnable call) {
        commonEventLoop.post(call);
    }

    public void delayPost(Runnable call, int nMillSec) {
        commonEventLoop.delayPost(call, nMillSec);
    }

    public void shutdown() {
        commonEventLoop.shutdown();
    }
}
