package notifications;

import android.content.DialogInterface;

import main.Route;
import main.ProjectAR;
import states.MapState;
import utils.RouteInfoDialog;
import fraguel.android.FRAGUEL;
import fraguel.android.Route;
import fraguel.android.states.MapState;
import fraguel.android.utils.RouteInfoDialog;

public class StartRouteNotification implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        RouteInfoDialog routeDialog;
        switch (which) {
            case 0:
                MapState.getInstance().setContextMenuDisplayed(false);
                MapState.getInstance()
                        .getGPS()
                        .startRoute(MapState.getInstance().getRoute(),
                                MapState.getInstance().getRoute().pointsOI.get(0));
                routeDialog = new RouteInfoDialog(ProjectAR.getInstance(), MapState
                        .getInstance().getRoute(), true);
                MapState.getInstance().setRouteInfoDialog(routeDialog);
                routeDialog.show();
                break;
            case 1:
                MapState.getInstance().setContextMenuDisplayed(false);
                MapState.getInstance()
                        .getGPS()
                        .startRoute(MapState.getInstance().getRoute(),
                                MapState.getInstance().getPointOI());
                routeDialog = new RouteInfoDialog(ProjectAR.getInstance(), MapState
                        .getInstance().getRoute(), true);
                MapState.getInstance().setRouteInfoDialog(routeDialog);
                routeDialog.show();
                break;
            case 2:
                MapState.getInstance().setContextMenuDisplayed(false);
                int i = 0;

                for (Route r : ProjectAR.getInstance().routes) {
                    MapState.getInstance().rutas[i] = r.name;
                    i++;
                }

                FRAGUEL.getInstance()
                        .createDialog("Escolher rota", MapState.getInstance().rutas,
                                new RouteSelectionNotification(),
                                new BackKeyNotification());

                break;

        }
        dialog.dismiss();
    }

}
