package threads;

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

import android.os.Message;
import android.util.Log;

//import com.google.android.maps.GeoPoint;
import main.ProjectAR;
import main.PointOI;
import states.MapState;

public class RouteThread extends Thread {

    private ArrayList<ArrayList<GeoPoint>> rutasVerde;
    private ArrayList<ArrayList<GeoPoint>> rutasRojo;

    private ArrayList<PointOI> visited = null;
    private ArrayList<PointOI> notVisited = null;

    private ArrayList<GeoPoint> rutaActual;
    private GeoPoint anterior, actual;
    private boolean first;

    public RouteThread(ArrayList<ArrayList<GeoPoint>> verde,
                       ArrayList<ArrayList<GeoPoint>> rojo) {
        super();

        first = false;
        visited = MapState.getInstance().getGPS().getRoutePointsVisited();
        notVisited = MapState.getInstance().getGPS().getRoutePointsNotVisited();
        rutasVerde = verde;
        rutasRojo = rojo;

    }

    @Override
    public void run() {
        boolean ok;
        for (PointOI p : visited) {
            if (first) {
                anterior = actual;
                actual = new GeoPoint((int) (p.coords[0] * 1000000),
                        (int) (p.coords[1] * 1000000));
                rutaActual = new ArrayList<GeoPoint>();
                ok = GetPath(anterior, actual, rutaActual);
                if (!ok) {
                    rutaActual.clear();
                    rutaActual.add(anterior);
                    rutaActual.add(actual);
                }
                rutasRojo.add(rutaActual);
            } else {
                first = true;
                actual = new GeoPoint((int) (p.coords[0] * 1000000),
                        (int) (p.coords[1] * 1000000));
            }

        }

        for (PointOI p : notVisited) {
            if (first) {
                anterior = actual;
                actual = new GeoPoint((int) (p.coords[0] * 1000000),
                        (int) (p.coords[1] * 1000000));
                rutaActual = new ArrayList<GeoPoint>();
                ok = this.GetPath(anterior, actual, rutaActual);
                if (!ok) {
                    rutaActual.clear();
                    rutaActual.add(anterior);
                    rutaActual.add(actual);
                }
                rutasVerde.add(rutaActual);
            } else {
                first = true;
                actual = new GeoPoint((int) (p.coords[0] * 1000000),
                        (int) (p.coords[1] * 1000000));
            }

        }

        Message m = new Message();
        m.arg2 = 0;
        ProjectAR.getInstance().routeHandler.sendMessage(m);

    }

    private boolean GetPath(GeoPoint src, GeoPoint dest,
                            ArrayList<GeoPoint> ruta) {
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
                // src
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
        }

        return result;

    }

}
