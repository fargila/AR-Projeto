package notifications;

import android.content.DialogInterface;
import main.ProjectAR;
import states.MapState;

public class StopRouteNotification implements DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface arg0, int arg1) {
        MapState.getInstance().setContextMenuDisplayed(false);
        MapState.getInstance().getGPS().stopRoute();
        ProjectAR.getInstance().stopTalking();
    }
}