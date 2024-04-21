package notifications;

import android.content.DialogInterface;

public class WarningNotificationButton implements
        DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

}
