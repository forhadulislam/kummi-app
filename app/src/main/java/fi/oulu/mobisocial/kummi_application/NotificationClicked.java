package fi.oulu.mobisocial.kummi_application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationClicked extends BroadcastReceiver {
    private static final String TAG = "TASKERI NOTIFICATIONCL";
    private DatabaseHandler db;
    public static String ID = "id";

    public void onReceive(Context context, Intent intent) {

        db = new DatabaseHandler(context);

        long id = intent.getLongExtra(ID, -1);
        Log.d(TAG, String.format("in onReceive id:%d", id));
        db.deleteTask(id);
        context.sendBroadcast(new Intent("TASK_DELETED"));
    }

}