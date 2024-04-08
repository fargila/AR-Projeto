package resources;

import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.file.Files;
import java.util.ArrayList;

import main.PointOI;
import main.ProjectAR;

public class ResourceManager {

    private static ResourceManager instance;
    private boolean initialized;

    private String rootPath;

    private final XMLManager xmlManager;

    private ResourceManager() {
        initialized = false;

        xmlManager = new XMLManager();
    }

    public static ResourceManager getInstance() {
        if (instance == null)
            instance = new ResourceManager();

        return instance;
    }

    private void createDirs(File rootSD) {
        rootSD.mkdir();
        new File(rootSD.getAbsolutePath() + "/ar").mkdir();
        new File(rootSD.getAbsolutePath() + "/config").mkdir();
        new File(rootSD.getAbsolutePath() + "/user").mkdir();
        new File(rootSD.getAbsolutePath() + "/routes").mkdir();
        new File(rootSD.getAbsolutePath() + "/tmp").mkdir();
    }

    public void initialize(final String root) {
        try {
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state))
                throw new Exception("SD Card not avaliable");

            File sd = Environment.getExternalStorageDirectory();
            rootPath = sd.getAbsolutePath() + "/" + root;

            if ((!sd.canRead()) || (!sd.canWrite()))
                throw new Exception("SD Card not avaliable");

            Log.d("FRAGUEL", "SD Card ready");

            File rootSD = new File(rootPath);
            if (!rootSD.exists())
                createDirs(rootSD);

            xmlManager.setRoot(root);
            initialized = true;
        } catch (Exception e) {
            Log.d("FRAGUEL", "Error: " + e);
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public String getRootPath() {
        return rootPath;
    }

    public XMLManager getXmlManager() {
        return xmlManager;
    }

    public void createXMLTemplate(String fileName, String routeName,
                                  int routeId, int numPoints) {

        File file = new File(ResourceManager.getInstance().getRootPath() + "/user/" + fileName + ".xml");

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fileos = null;

        try {
            fileos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        XmlSerializer serializer = Xml.newSerializer();

        try {

            serializer.setOutput(fileos, "UTF-8");
            serializer.startDocument(null, null);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startTag(null, "route");
            serializer.attribute(null, "id", Integer.toString(routeId));

            serializer.startTag(null, "name");
            serializer.text(routeName);
            serializer.endTag(null, "name");
            serializer.startTag(null, "description");
            serializer.endTag(null, "description");
            serializer.startTag(null, "icon");
            serializer.endTag(null, "icon");

            serializer.startTag(null, "points");

            for (int i = 0; i < numPoints; i++)
            {
                serializer.startTag(null, "point");
                serializer.attribute(null, "id", Integer.toString(i));
                serializer.attribute(null, "version", "1.0");

                serializer.startTag(null, "coords");
                serializer.attribute(null, "x", "0");
                serializer.attribute(null, "y", "0");
                serializer.endTag(null, "coords");

                serializer.startTag(null, "title");
                serializer.endTag(null, "title");

                serializer.startTag(null, "pointdescription");
                serializer.endTag(null, "pointdescription");

                serializer.startTag(null, "pointicon");
                serializer.endTag(null, "pointicon");

                serializer.startTag(null, "images");
                serializer.startTag(null, "image");
                serializer.attribute(null, "url", "");
                serializer.endTag(null, "image");
                serializer.endTag(null, "images");

                serializer.startTag(null, "video");
                serializer.endTag(null, "video");

                serializer.startTag(null, "ar");
                serializer.attribute(null, "lat", "0.0");
                serializer.attribute(null, "long", "0.0");
                serializer.attribute(null, "alt", "0.0");
                serializer.attribute(null, "file", "");
                serializer.endTag(null, "ar");

                serializer.endTag(null, "point");

            }

            serializer.endTag(null, "points");
            serializer.endTag(null, "route");
            serializer.endDocument();
            serializer.flush();
            assert fileos != null;
            fileos.close();
            Toast.makeText(ProjectAR.getInstance().getApplicationContext(), "Plantilla Creada", Toast.LENGTH_SHORT).show();

        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

    }

    public void createXMLFromPoints(String fileName, String routeName, int routeId, ArrayList<PointOI> points) {

        File file = new File(ResourceManager.getInstance().getRootPath() + "/user/" + fileName + ".xml");

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fileos = null;

        try {
            fileos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        XmlSerializer serializer = Xml.newSerializer();

        try {

            serializer.setOutput(fileos, "UTF-8");
            serializer.startDocument(null, null);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startTag(null, "route");
            serializer.attribute(null, "id", Integer.toString(routeId));

            serializer.startTag(null, "name");
            serializer.text(routeName);
            serializer.endTag(null, "name");
            serializer.startTag(null, "description");
            serializer.endTag(null, "description");
            serializer.startTag(null, "icon");
            serializer.endTag(null, "icon");

            serializer.startTag(null, "points");

            for (PointOI p : points)
            {
                serializer.startTag(null, "point");
                serializer.attribute(null, "id", Integer.toString(p.id));
                serializer.attribute(null, "version", "1.0");

                serializer.startTag(null, "coords");
                serializer.attribute(null, "x", Float.toString(p.coords[0]));
                serializer.attribute(null, "y", Float.toString(p.coords[1]));
                serializer.endTag(null, "coords");

                serializer.startTag(null, "title");
                serializer.text(p.title);
                serializer.endTag(null, "title");

                serializer.startTag(null, "pointdescription");
                serializer.endTag(null, "pointdescription");

                serializer.startTag(null, "pointicon");
                serializer.endTag(null, "pointicon");

                serializer.startTag(null, "images");
                serializer.startTag(null, "image");
                serializer.attribute(null, "url", "");
                serializer.endTag(null, "image");
                serializer.endTag(null, "images");

                serializer.startTag(null, "video");
                serializer.endTag(null, "video");

                serializer.startTag(null, "ar");
                serializer.attribute(null, "lat", "0.0");
                serializer.attribute(null, "long", "0.0");
                serializer.attribute(null, "alt", "0.0");
                serializer.attribute(null, "file", "");
                serializer.endTag(null, "ar");

                serializer.endTag(null, "point");

            }

            serializer.endTag(null, "points");

            serializer.endTag(null, "route");

            serializer.endDocument();
            serializer.flush();
            assert fileos != null;
            fileos.close();
            Toast.makeText(ProjectAR.getInstance().getApplicationContext(), "Plantilla Creada", Toast.LENGTH_SHORT).show();

        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

    }

    public void toTempFile() {
        if (ProjectAR.getInstance().getCurrentState().getId() == MainMenuState.STATE_ID)
        {
            MainMenuState state = (MainMenuState) ProjectAR.getInstance().getCurrentState();
            File file = new File(ResourceManager.getInstance().getRootPath() + "/user/" + state.getRouteName() + ".tmp");
            if (file.exists())
            {
                file.delete();
                Toast.makeText(ProjectAR.getInstance().getApplicationContext(), "El archivo ya existía y se ha sobrescrito", Toast.LENGTH_SHORT).show();
            }
            try {
                ObjectOutputStream oos = new ObjectOutputStream(
                        new FileOutputStream(file));
                for (PointOI p : state.getGeoTaggingPoints())
                    oos.writeObject(p);

                oos.close();
            } catch (FileNotFoundException e) {
                Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                                "Error al grabar el archivo", Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                                "Error al grabar el archivo", Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            }
            Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                    "Datos guardados con éxito", Toast.LENGTH_SHORT).show();
        }
    }

    public void fromTmpFile(String path, String routeName) {
        if (ProjectAR.getInstance().getCurrentState().getId() == MainMenuState.STATE_ID)
        {
            MainMenuState state = (MainMenuState) ProjectAR.getInstance().getCurrentState();
            ArrayList<PointOI> points = new ArrayList<PointOI>();
            state.setRouteName(routeName);
            File f = new File(path);
            ObjectInputStream ois = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ois = new ObjectInputStream(Files.newInputStream(f.toPath()));
                }

                // Se lee el primer objeto
                Object aux = ois.readObject();

                // Mientras haya objetos
                while (aux != null)
                {
                    if (aux instanceof PointOI)
                    {
                        points.add((PointOI) aux);
                        aux = ois.readObject();
                    }
                }
                ois.close();
                state.setGeoTaggingPoints(points);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                assert ois != null;
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            state.setGeoTaggingPoints(points);

        }

    }

}
