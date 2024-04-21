package maps;

//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.OverlayItem;

import main.Route;
import main.PointOI;


public class PointOverlay extends OverlayItem {

    private PointOI p;
    private Route r;

    public PointOverlay(GeoPoint arg0, String arg1, String arg2) {
        super(arg0, arg1, arg2);
        p = null;
        r = null;

    }

    public PointOverlay(GeoPoint arg0, String arg1, String arg2, PointOI point,
                        Route route) {
        super(arg0, arg1, arg2);
        p = point;
        r = route;

    }

    public Route getRoute() {
        return r;
    }

    public PointOI getPointOI() {
        return p;
    }

}

