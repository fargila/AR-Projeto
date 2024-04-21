package notifications;

import android.content.DialogInterface;
import states.MapState;
import utils.RouteInfoDialog;

public class ProximityAlertNotificationButton implements
        DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == 0)
            MapState.getInstance()
                    .getGPS()
                    .startRoute(
                            MapState.getInstance().getContextRoute(),
                            MapState.getInstance().getContextRoute().pointsOI
                                    .get(0));
        else
            MapState.getInstance()
                    .getGPS()
                    .startRoute(
                            MapState.getInstance().getContextRoute(),
                            MapState.getInstance().getContextRoute().pointsOI
                                    .get(which - 1));

        RouteInfoDialog routeDialog = new RouteInfoDialog(
                FRAGUEL.getInstance(),
                MapState.getInstance().getContextRoute(), true);
        MapState.getInstance().setRouteInfoDialog(routeDialog);
        routeDialog.show();
        MapState.getInstance().setContextRoute(null);
        dialog.dismiss();
    }

}
