package com.jeeni.content.classic.Interfaces;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public interface SyncCallBacks {

    void onSyncFinished();
    void onSyncFailed(Exception e);
}
