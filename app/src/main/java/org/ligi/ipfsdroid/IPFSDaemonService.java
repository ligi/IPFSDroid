package org.ligi.ipfsdroid;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class IPFSDaemonService extends IntentService {

    private NotificationManager nManager;
    int NOTIFICATION_ID = 12345;

    public IPFSDaemonService() {
        super("IPFSDaemonService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Intent exitIntent = new Intent(this, IPFSDaemonService.class);
        exitIntent.setAction("STOP");
        PendingIntent pendingExit = PendingIntent.getService(this, 0, exitIntent, 0);

        Intent targetIntent = new Intent(this, DetailsActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, targetIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setOngoing(true)
                                                                                 .setSmallIcon(R.drawable.notification)
                                                                                 .setContentTitle("IPFS Daemon")
                                                                                 //.addAction(R.drawable.ic_navigation_check, "exit", pendingExit)
                                                                                 .setContentText("The daemon is running");


        builder.setContentIntent(pIntent);
        nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());

        try {
            new IPFSDaemon(getBaseContext()).run("daemon").waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (nManager!=null) {
            nManager.cancel(NOTIFICATION_ID);
        }

    }


    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final String action = intent.getAction();
        if (nManager!=null && action != null && action.equals("STOP")) {
            // TODO actually stop the daemon https://github.com/ipfs/faq/issues/39
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
