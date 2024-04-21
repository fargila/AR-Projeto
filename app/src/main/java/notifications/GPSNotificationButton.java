package notifications;

import main.ProjectAR;
import android.content.DialogInterface;
import android.content.Intent;

public class GPSNotificationButton implements DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ProjectAR.getInstance().startActivity(gpsOptionsIntent);
    }

}
