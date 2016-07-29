package com.dqhuynh.gplace.callback;

/**
 * Created by Administrator on 6/26/2015.
 */
public interface LocationListenerCallback {
    public abstract void onLocationReceiver();
    public abstract void onLocationFailed();
}
