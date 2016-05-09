package org.ligi.ipfsdroid;

import android.app.IntentService;
import android.content.Intent;

public class IPFSDaemonService extends IntentService {

    public IPFSDaemonService() {
        super("IPFSDaemonService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        new IPFSBinaryController(getBaseContext()).run("daemon");
    }
}
