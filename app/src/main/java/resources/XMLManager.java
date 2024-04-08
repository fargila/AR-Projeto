package resources;

import android.os.Environment;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Objects;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import main.PointOI;
import main.Route;

public class XMLManager {

    private XMLReader parser;
    private File root;

    XMLManager() {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp;
            sp = spf.newSAXParser();
            parser = sp.getXMLReader();
            root = null;
        } catch (Exception e) {
            Log.d("FRAGUEL", "Error", e);
        }
    }

    public void setRoot(String root) {
        File sd = Environment.getExternalStorageDirectory();
        String finalRoot = root;
        root = String.valueOf(Objects.requireNonNull(sd.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.equals(finalRoot);
            }
        }))[0]);
    }

    public ArrayList<PointOI> readPointsOI(final String fileName) {
        try {
            File pointsFile = Objects.requireNonNull(Objects.requireNonNull(root.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return filename.equals("routes");
                }
            }))[0].listFiles(new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return filename.equals(fileName + ".xml");
                }
            }))[0];
            FileInputStream pointsStream = new FileInputStream(pointsFile);
            PointsHandler ph = new PointsHandler();
            parser.setContentHandler(ph);
            parser.parse(new InputSource(pointsStream));
            return ph.getParsedData();
        } catch (Exception e) {
            Log.d("FRAGUEL", "Error", e);
        }
        return null;
    }

    public Route readRoute(final String fileName) {
        try {
            File routesFile = Objects.requireNonNull(Objects.requireNonNull(root.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return filename.equals("routes");
                }
            }))[0].listFiles(new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return filename.equals(fileName + ".xml");
                }
            }))[0];
            FileInputStream routesStream = new FileInputStream(routesFile);
            RouteHandler rh = new RouteHandler();
            parser.setContentHandler(rh);
            parser.parse(new InputSource(routesStream));
            return rh.getParsedData();
        } catch (Exception e) {
            Log.d("FRAGUEL", "Error", e);
        }
        return null;
    }

    public ArrayList<MinRouteInfo> readAvailableRoutes(final String fileName) {
        try {
            File routesFile = new File(this.root + "/" + fileName);
            FileInputStream routesStream = new FileInputStream(routesFile);
            MinRouteInfoHandler rh = new MinRouteInfoHandler();
            parser.setContentHandler(rh);
            parser.parse(new InputSource(routesStream));
            return rh.getParsedData();
        } catch (Exception e) {
            Log.d("ProjetoAR", "Error", e);
        }
        return null;
    }

}
