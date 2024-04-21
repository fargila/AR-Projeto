package GPS;

import java.util.ArrayList;

import android.location.Location;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import main.*;
import notifications.WarningNotificationButton;
import states.MapState;
import states.PointInfoState;

//import fraguel.android.R;


public class GPSProximityRouteListener extends GPSProximity {

    private ArrayList<PointOI> pointsToVisit;
    boolean noMoreMessages;

    public GPSProximityRouteListener() {

        super();
        pointsToVisit = new ArrayList<PointOI>();
    }

    @Override
    public synchronized void onLocationChanged(Location location) {

        if (!noMoreMessages) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();
            distance = Float.MAX_VALUE;

            if (pointsToVisit.size() == 0) {
                // ruta terminada
                MapState.getInstance().removeAllPopUps();
                FRAGUEL.getInstance().createOneButtonNotification(
                        "Ruta finalizada",
                        "Ha completado todos los puntos de interés de la ruta "
                                + currentRoute.name,
                        new WarningNotificationButton());
                noMoreMessages = true;

            } else {
                if (MapState.getInstance().getGPS().isDialogDisplayed()) {
                    PointInfoState state = (PointInfoState) FRAGUEL
                            .getInstance().getCurrentState();
                    Location.distanceBetween(latitude, longitude,
                            state.getPointOI().coords[0],
                            state.getPointOI().coords[1], results);
                    // si está fuera de rango cargamos el mapa
                    float offset = Math.abs(lastFix - results[0]);
                    if (offset <= 10.0f) {
                        lastFix = results[0];
                        hasBeenVisited = true;
                    } else
                        hasBeenVisited = false;
                    if ((results[0] > proximityAlertDistance
                            + proximityAlertError)
                            && hasBeenVisited) {
                        FRAGUEL.getInstance().changeState(MapState.STATE_ID);
                        MapState.getInstance().loadData(currentRoute,
                                currentPoint);
                        MapState.getInstance().getGPS()
                                .setDialogDisplayed(false);
                    }
                } else if (!MapState.getInstance().getGPS().isDialogDisplayed()
                        && FRAGUEL.getInstance().getCurrentState().getId() == MapState.STATE_ID) {
                    // miramos la distancia al siguiente punto a visitar en la
                    // ruta
                    Location.distanceBetween(latitude, longitude,
                            pointsToVisit.get(0).coords[0],
                            pointsToVisit.get(0).coords[1], results);
                    distance = results[0];
                    if (results[0] <= proximityAlertDistance) {
                        currentPoint = pointsToVisit.get(0);

                        // actualizar las listas y el MapState
                        pointsVisited.add(currentPoint);
                        pointsToVisit.remove(0);
                        MapState.getInstance().refreshMapRouteMode();

                        // cambiamos de estado
                        MapState.getInstance().getGPS()
                                .setDialogDisplayed(true);
                        super.mediaNotification();
                        lastFix = results[0];
                        FRAGUEL.getInstance().changeState(
                                PointInfoState.STATE_ID);
                        FRAGUEL.getInstance().getCurrentState()
                                .loadData(currentRoute, currentPoint);

                    } else {
                        // mostrar info de la distancia y el bearing si no hay
                        // ningun popup
                        ((TextView) MapState.getInstance().getPopupOnRoute()
                                .findViewById(R.id.popuponroute_texto1))
                                .setText((int) distance
                                        + " metros para llegar a "
                                        + pointsToVisit.get(0).title);
                        // elegimos la flecha correspondiente al bearing(ángulo
                        // de giro)
                        if (results[1] <= 22.5 && results[1] > -22.5)
                            ((ImageView) MapState
                                    .getInstance()
                                    .getPopupOnRoute()
                                    .findViewById(R.id.popuponroute_orientation))
                                    .setImageResource(R.drawable.flecha_norte);
                        else if (results[1] <= 67.5 && results[1] > 22.5)
                            ((ImageView) MapState
                                    .getInstance()
                                    .getPopupOnRoute()
                                    .findViewById(R.id.popuponroute_orientation))
                                    .setImageResource(R.drawable.flecha_norte_este);
                        else if (results[1] <= 112.5 && results[1] > 67.5)
                            ((ImageView) MapState
                                    .getInstance()
                                    .getPopupOnRoute()
                                    .findViewById(R.id.popuponroute_orientation))
                                    .setImageResource(R.drawable.flecha_este);
                        else if (results[1] <= 157.5 && results[1] > 112.5)
                            ((ImageView) MapState
                                    .getInstance()
                                    .getPopupOnRoute()
                                    .findViewById(R.id.popuponroute_orientation))
                                    .setImageResource(R.drawable.flecha_sur_este);
                        else if (results[1] <= -157.5 && results[1] > 157.5)
                            ((ImageView) MapState
                                    .getInstance()
                                    .getPopupOnRoute()
                                    .findViewById(R.id.popuponroute_orientation))
                                    .setImageResource(R.drawable.flecha_sur);
                        else if (results[1] <= -112.5 && results[1] > -157.5)
                            ((ImageView) MapState
                                    .getInstance()
                                    .getPopupOnRoute()
                                    .findViewById(R.id.popuponroute_orientation))
                                    .setImageResource(R.drawable.flecha_sur_oeste);
                        else if (results[1] <= -67.5 && results[1] > -112.5)
                            ((ImageView) MapState
                                    .getInstance()
                                    .getPopupOnRoute()
                                    .findViewById(R.id.popuponroute_orientation))
                                    .setImageResource(R.drawable.flecha_oeste);
                        else if (results[1] <= -22.5 && results[1] > -67.5)
                            ((ImageView) MapState
                                    .getInstance()
                                    .getPopupOnRoute()
                                    .findViewById(R.id.popuponroute_orientation))
                                    .setImageResource(R.drawable.flecha_norte_oeste);
                        // mostramos el pop-up
                        if (FRAGUEL.getInstance().getCurrentState().getId() == MapState.STATE_ID
                                && !MapState.getInstance().isAnyPopUp())
                            MapState.getInstance().setPopupOnRoute();
                        else
                            ((FrameLayout) MapState.getInstance()
                                    .getPopupOnRoute()
                                    .findViewById(R.id.popuponroute))
                                    .invalidate();

                    }

                }
            }
        }
    }

    public void startRoute(Route selectedRoute, PointOI pointToStart) {
        pointsToVisit.clear();
        pointsVisited.clear();
        boolean limit = false;
        currentRoute = selectedRoute;
        for (PointOI point : currentRoute.pointsOI) {
            if (!limit) {
                if (point.id == pointToStart.id) {
                    limit = true;
                    pointsToVisit.add(point);
                } else {
                    pointsVisited.add(point);
                }

            } else {
                pointsToVisit.add(point);
            }
        }

        MapState.getInstance().startRoute();
        noMoreMessages = false;
    }

    public ArrayList<PointOI> pointsVisited() {
        return pointsVisited;
    }

    public ArrayList<PointOI> pointsToVisit() {
        return pointsToVisit;
    }

}
