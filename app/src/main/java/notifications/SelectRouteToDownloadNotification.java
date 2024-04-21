package notifications;

import android.content.DialogInterface;
import android.widget.Toast;
import main.ProjectAR;
import resources.ResourceManager;
import states.RouteManagerState;
import threads.FileDownloadingThread;
import main.MinRouteInfo;


public class SelectRouteToDownloadNotification implements
        DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface arg0, int which) {
        RouteManagerState state = (RouteManagerState) ProjectAR.getInstance()
                .getCurrentState();

        MinRouteInfo routeToDownload = state.getAllRoutesAvailables()
                .get(which);
        String[] urls;
        String[] names;
        if (routeToDownload.urlArFiles != null) {
            urls = new String[routeToDownload.urlArFiles.length + 1];
            names = new String[urls.length];

            urls[0] = routeToDownload.urlXml;
            names[0] = "route" + routeToDownload.id + ".xml";

            int i = 1;
            String[] name;
            for (String s : routeToDownload.urlArFiles) {
                urls[i] = s;
                name = s.split("/");
                names[i] = name[name.length - 1];
                i++;
            }
        } else {
            urls = new String[1];
            names = new String[1];
            urls[0] = routeToDownload.urlXml;
            names[0] = "route" + routeToDownload.id + ".xml";

        }
        FileDownloadingThread t = new FileDownloadingThread(urls, names,
                ResourceManager.getInstance().getRootPath() + "/ar/");
        Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                "Descargando ruta y archivos asociados. Espere por favor.",
                Toast.LENGTH_LONG).show();
        t.start();
    }

}
