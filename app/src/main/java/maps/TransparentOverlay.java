package maps;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapView;
//import com.google.android.maps.Overlay;
//import com.google.android.maps.Projection;

import main.PointOI;
import main.Route;
import threads.RouteThread;


public class RouteOverlay extends Overlay {

    private Route route = null;
    private ArrayList<ArrayList<GeoPoint>> rutasVerde;
    private ArrayList<ArrayList<GeoPoint>> rutasRojo;

    public RouteOverlay(Route r) {
        route = r;
    }

    public RouteOverlay() {
        rutasVerde = new ArrayList<ArrayList<GeoPoint>>();
        rutasRojo = new ArrayList<ArrayList<GeoPoint>>();
        route = null;
        RouteThread thread = new RouteThread(rutasVerde, rutasRojo);
        thread.start();
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        Projection projection = mapView.getProjection();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        Point actual = null;
        Point anterior = null;
        paint.setStrokeWidth(5);
        paint.setAlpha(120);
        boolean first = false;

        if (route == null) {
            paint.setColor(Color.RED);

            for (ArrayList<GeoPoint> r : rutasRojo) {

                if (r != null) {
                    for (GeoPoint p : r) {

                        anterior = actual;
                        actual = new Point();
                        projection.toPixels(p, actual);
                        if (first) {
                            canvas.drawLine(actual.x, actual.y, anterior.x,
                                    anterior.y, paint);
                        } else
                            first = true;

                    }
                }

            }
            paint.setColor(Color.GREEN);
            for (ArrayList<GeoPoint> r : rutasVerde) {
                if (r != null) {
                    for (GeoPoint p : r) {
                        anterior = actual;
                        actual = new Point();
                        projection.toPixels(p, actual);
                        if (first) {
                            canvas.drawLine(actual.x, actual.y, anterior.x,
                                    anterior.y, paint);
                        } else
                            first = true;

                    }
                }

            }

        } else {
            paint.setColor(Color.MAGENTA);
            for (PointOI p : route.pointsOI) {
                anterior = actual;
                actual = new Point();
                projection.toPixels(new GeoPoint((int) (p.coords[0] * 1000000),
                        (int) (p.coords[1] * 1000000)), actual);
                if (first) {
                    canvas.drawLine(actual.x, actual.y, anterior.x, anterior.y,
                            paint);
                } else
                    first = true;

            }
        }
        super.draw(canvas, mapView, shadow);

    }

}
