package GPS;

import android.location.Location;

import main.PointOI;
import main.ProjectAR;
import main.Route;
import states.MapState;
import states.PointInfoState;

public class gpsProximityListener extends gpsProximity{

    public gpsProximityListener(){
        super();
    }

    public synchronized void onLocationChanged(Location location){
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        distance = Float.MAX_VALUE;

        if(MapState.getInstance().getGPS().isDialogDisplayed()){
            PointInfoState state = (PointInfoState) ProjectAR.getInstance().getCurrentState();
            Location.distanceBetween(latitude, longitude, state.getPointOI.coords[0], state.getPointOI().coords[1], results);

            float offset = Math.abs(lastFix - results[0]){
                if(offset <= 10.0f)
                {
                    lastFix = results[0];
                    hasBeenVisited = true;
                }
                else
                    hasBeenVisited = false;
                if(results[0] > proximityAlertDistance + proximityAlertError && hasBeenVisited)
                {
                    ProjectAR.getInstance().changeState(MapState.STATE_ID);
                    MapState.getInstance().loadData(currentRoute, currentPoint);
                    MapState.getInstance().getGPS().setDialogDisplayed(false);
                }
                else if(!MapState.getInstance().getGPS().isDialogDisplayed() &&
                        ProjectAR.getInstance().getCurrentState().getState().getId() == MapState.STATE_ID)
                {
                    for(Route r : ProjectAR.getInstance().routes){
                        for(PointOI p : r.pointsOI)
                        {
                            Location.distanceBetween(latitude, longitude, p.coords[0], p.coords[1], results);
                            if(results[0] <= proximityAlertDistance)
                            {
                                if(results[0] <= distance)
                                {
                                    currentRoute = r;
                                    currentPoint = p;
                                    distance = results[0];
                                }
                            }
                        }
                    }
                    if(currentRoute != null &&
                            currentPoint != null &&
                            !MapState.getInstance().getGPS().isDialogDisplayed())
                    {
                        super.mediaNotification();
                        ProjectAR.getInstance().changeState(PointInfoState.STATE_ID);
                        ProjectAR.getInstance().getCurrentState().loadData(currentRoute, currentPoint);
                        MapState.getInstance().getGPS().setDialogDisplayed(true);
                        lastFix = results[0];
                    }
                }
            }
        }
    }
}
