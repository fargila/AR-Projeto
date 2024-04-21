package notifications;

import android.content.DialogInterface;

public class ErrorNotificationButton implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        System.exit(0);
    }

}

