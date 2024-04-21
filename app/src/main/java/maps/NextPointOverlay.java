package maps;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapView;
//import com.google.android.maps.Overlay;
//import com.google.android.maps.Projection;

import states.MapState;

public class NextPointOverlay extends Overlay {

    private ArrayList<GeoPoint> ruta;
    private boolean fetchPath, first;

    public NextPointOverlay() {
        ruta = new ArrayList<GeoPoint>();
        fetchPath = GetPath(
                MapState.getInstance().getMyLocation(),
                new GeoPoint(
                        (int) (MapState.getInstance().getGPS()
                                .getRoutePointsNotVisited().get(0).coords[0] * 1000000),
                        (int) (MapState.getInstance().getGPS()
                                .getRoutePointsNotVisited().get(0).coords[1] * 1000000)));
        first = false;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (MapState.getInstance().getGPS().isRouteMode()) {
            Projection projection = mapView.getProjection();
            Paint paint = new Paint();
            paint.setStrokeWidth(3);
            paint.setAlpha(120);
            paint.setColor(Color.BLUE);
            Point src = null;
            Point target = null;
            if (fetchPath) {
                for (GeoPoint p : ruta) {
                    src = target;
                    target = new Point();
                    projection.toPixels(p, target);
                    if (first) {
                        canvas.drawLine(target.x, target.y, src.x, src.y, paint);
                    } else
                        first = true;

                }
            } else {
                src = new Point();
                target = new Point();
                projection
                        .toPixels(MapState.getInstance().getMyLocation(), src);
                projection
                        .toPixels(
                                new GeoPoint((int) (MapState.getInstance()
                                        .getGPS().getRoutePointsNotVisited()
                                        .get(0).coords[0] * 1000000),
                                        (int) (MapState.getInstance().getGPS()
                                                .getRoutePointsNotVisited()
                                                .get(0).coords[1] * 1000000)),
                                target);
                canvas.drawLine(src.x, src.y, target.x, target.y, paint);
            }
            first = false;
            super.draw(canvas, mapView, shadow);
        }

    }

    private boolean GetPath(GeoPoint src, GeoPoint dest) {
        boolean result = false;
        // connect to map web service
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.google.com/maps?f=d&hl=en");
        urlString.append("&saddr=");// from
        urlString.append(Double.toString((double) src.getLatitudeE6() / 1.0E6));
        urlString.append(",");
        urlString
                .append(Double.toString((double) src.getLongitudeE6() / 1.0E6));
        urlString.append("&daddr=");// to
        urlString
                .append(Double.toString((double) dest.getLatitudeE6() / 1.0E6));
        urlString.append(",");
        urlString
                .append(Double.toString((double) dest.getLongitudeE6() / 1.0E6));
        urlString.append("&dirflg=w");
        urlString.append("&ie=UTF8&0&om=0&output=kml");
        Log.d("xxx", "URL=" + urlString.toString());
        // get the kml (XML) doc. And parse it to get the coordinates(direction
        // route).
        Document doc = null;
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL(urlString.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(urlConnection.getInputStream());

            if (doc.getElementsByTagName("GeometryCollection").getLength() > 0) {
                result = true;
                String path = doc.getElementsByTagName("GeometryCollection")
                        .item(0).getFirstChild().getFirstChild()
                        .getFirstChild().getNodeValue();
                String[] pairs = path.split(" ");
                String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude
                // lngLat[1]=latitude
                // lngLat[2]=height
                GeoPoint gp1;
                ruta.add(src);
                for (int i = 0; i < pairs.length; i++) // the last one would be
                // crash
                {
                    lngLat = pairs[i].split(",");
                    // watch out! For GeoPoint, first:latitude, second:longitude
                    gp1 = new GeoPoint(
                            (int) (Double.parseDouble(lngLat[1]) * 1E6),
                            (int) (Double.parseDouble(lngLat[0]) * 1E6));
                    ruta.add(gp1);
                }
                ruta.add(dest);

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
            Log.d("xxx", e.getMessage());
            Log.d("xxx", e.getLocalizedMessage());
        }

        return result;

    }
}
