package states;

import java.io.File;
import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import lists.RouteManagerAdapter;
import main.*;
//import fraguel.android.R;
import resources.ResourceManager;
import threads.FileDownloadingThread;
import threads.ImageDownloadingThread;
import utils.RouteInfoDialog;
import utils.TitleTextView;
import notifications.SelectRouteToDownloadNotification;
//import fraguel.android.lists.RouteManagerAdapter;
//import fraguel.android.notifications.SelectRouteToDownloadNotification;

public class RouteManagerState extends State {

    public static final int STATE_ID = 8;
    private static RouteManagerState instance;
    // Variables de los botones del menu
    private static final int ROUTEMANAGERSTATE_ADDROUTE = 1;
    private static final int ROUTEMANAGERSTATE_DELETEROUTE = 2;

    private LinearLayout container;
    private RouteManagerAdapter adapter;
    private TitleTextView title;
    private ListView list;
    private ArrayList<String> currentDataTitle;
    private ArrayList<String> currentDataDescrip;
    // 0->routes,1->points,2->pointData
    private int internalState;
    private int selectedRoute, selectedPoint;
    private boolean displayRouteInfo;
    private RouteInfoDialog routeInfo = null;

    private ArrayList<MinRouteInfo> allRoutesAvailables = null;

    public RouteManagerState() {
        super();
        id = STATE_ID;
        // Singleton
        instance = this;
    }

    public static RouteManagerState getInstance() {
        if (instance == null)
            instance = new RouteManagerState();
        return instance;
    }

    @Override
    public void load() {

        container = new LinearLayout(ProjectAR.getInstance()
                .getApplicationContext());
        container.setOrientation(LinearLayout.VERTICAL);
        title = new TitleTextView(ProjectAR.getInstance().getApplicationContext());

        container.addView(title);
        loadRoutes(0);
        viewGroup = container;
        selectedRoute = 0;
        selectedPoint = 0;

        ProjectAR.getInstance().addView(viewGroup);
        ProjectAR.getInstance().registerForContextMenu(container);
        displayRouteInfo = false;
    }

    @Override
    public void onClick(View v) {

    }

    private void addItemClickListenerToListView() {

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                switch (internalState) {
                    case 0:
                        selectedRoute = position;
                        loadPoints(selectedRoute);
                        break;
                    case 1:
                        selectedPoint = position;
                        ProjectAR.getInstance().changeState(PointInfoState.STATE_ID);
                        Route r = ProjectAR.getInstance().routes.get(selectedRoute);
                        ProjectAR.getInstance().getCurrentState()
                                .loadData(r, r.pointsOI.get(selectedPoint));
                        break;
                }

            }

        });
    }

    private void addOnItemLongClickListenerToListView() {

        list.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {

                switch (internalState) {
                    case 0:
                        displayRouteInfo = true;
                        route = ProjectAR.getInstance().routes.get(position);
                        ProjectAR.getInstance().openContextMenu(container);
                        break;
                    case 1:
                        break;
                }
                return true;
            }

        });
    }

    private void loadPoints(int route) {
        System.gc();
        title.setText(title.getText() + " '"
                + ProjectAR.getInstance().routes.get(route).name + "'");
        container.removeView(list);
        setAdapter();
        currentDataTitle = new ArrayList<String>();
        currentDataDescrip = new ArrayList<String>();
        ArrayList<PointOI> points = ProjectAR.getInstance().routes.get(route).pointsOI;
        String[] urls = new String[points.size()];
        String[] names = new String[urls.length];
        int i = 0;
        for (PointOI p : points) {
            currentDataTitle.add(p.title);
            currentDataDescrip.add("");
            urls[i] = p.icon;
            names[i] = "point" + p.id + "icon";
            i++;
        }
        adapter.setTitle(currentDataTitle);
        adapter.setDescription(currentDataDescrip);
        adapter.notifyDataSetChanged();
        if (urls.length != 0) {
            this.imageThread = new ImageDownloadingThread(urls, names,
                    ResourceManager.getInstance().getRootPath() + "/tmp/route"
                            + ProjectAR.getInstance().routes.get(route).id + "/");
            imageThread.start();
        }
        internalState = 1;
        list.setSelection(selectedPoint);
    }

    private void loadRoutes(int routeFocus) {
        System.gc();
        container.removeView(list);
        title.setText(R.string.routemanagerstate_title_routes_spanish);
        setAdapter();
        currentDataTitle = new ArrayList<String>();
        currentDataDescrip = new ArrayList<String>();
        String[] urls = new String[ProjectAR.getInstance().routes.size()];
        String[] names = new String[urls.length];
        int i = 0;
        for (Route r : ProjectAR.getInstance().routes) {
            currentDataTitle.add(r.name);
            currentDataDescrip.add(r.description);
            urls[i] = r.icon;
            names[i] = "route" + r.id + "icon";
            i++;
        }
        adapter.setTitle(currentDataTitle);
        adapter.setDescription(currentDataDescrip);
        adapter.notifyDataSetChanged();
        if (urls.length != 0) {
            this.imageThread = new ImageDownloadingThread(urls, names,
                    ResourceManager.getInstance().getRootPath() + "/tmp/");
            imageThread.start();
        }
        internalState = 0;
        list.setSelection(routeFocus);

    }

    private void setAdapter() {
        list = new ListView(ProjectAR.getInstance().getApplicationContext());
        list.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        list.setCacheColorHint(0);
        list.setDrawSelectorOnTop(true);
        adapter = new RouteManagerAdapter(ProjectAR.getInstance().getApplicationContext());
        list.setAdapter(adapter);
        ColorDrawable divcolor = new ColorDrawable(Color.DKGRAY);
        list.setDivider(divcolor);
        list.setDividerHeight(2);
        addItemClickListenerToListView();
        addOnItemLongClickListenerToListView();
        container.addView(list);

    }

    @SuppressWarnings("static-access")
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == event.KEYCODE_BACK
                & event.getAction() == event.ACTION_DOWN) {
            if (internalState == 1) {
                loadRoutes(selectedRoute);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public Menu onCreateStateOptionsMenu(Menu menu) {
        // Borramos el menu de opciones anterior
        menu.clear();
        // Añadimos las opciones del menu
        menu.add(0, ROUTEMANAGERSTATE_ADDROUTE, 0,
                R.string.routemanagerstate_menu_addroute).setIcon(
                R.drawable.ic_menu_routeadd);
        menu.add(0, ROUTEMANAGERSTATE_DELETEROUTE, 0,
                R.string.routemanagerstate_menu_deleteroute).setIcon(
                R.drawable.ic_menu_routerem);

        return menu;
    }

    @Override
    public boolean onStateOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case ROUTEMANAGERSTATE_ADDROUTE:
                Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                        "Cargando... Espere por favor.", Toast.LENGTH_LONG).show();
                String[] url = { "http://www.blackmesa.es/fraguel/allroutes.xml" };
                String[] name = { "allroutes.xml" };
                FileDownloadingThread t = new FileDownloadingThread(url, name, "");
                t.start();
                return true;

            case ROUTEMANAGERSTATE_DELETEROUTE:
                ProjectAR.getInstance().openContextMenu(list);
                return true;
        }
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        ProjectAR.getInstance().closeContextMenu();
        if (item.getGroupId() == 0)
            deleteSelectedRoute(ProjectAR.getInstance().routes.get(item
                    .getItemId()).id);
        else {
            if (item.getItemId() == 0) {
                this.routeInfo = new RouteInfoDialog(ProjectAR.getInstance(),
                        route, false);
                routeInfo.show();
            } else
                deleteSelectedRoute(route.id);
        }

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {

        if (!displayRouteInfo) {
            menu.setHeaderTitle("Seleccione la ruta a eliminar");
            int i = 0;
            for (Route r : ProjectAR.getInstance().routes) {
                menu.add(0, i, 0, r.name);
                i++;
            }
        } else {
            menu.setHeaderTitle("Ruta: " + route.name);
            menu.add(1, 0, 0, "Mostrar descripción");
            menu.add(1, 1, 0, "Eliminar ruta");
            displayRouteInfo = false;
        }

    }

    public int getInternalState() {
        return internalState;
    }

    public int getSelectedRoute() {
        return selectedRoute;
    }

    @Override
    public void imageLoaded(int index) {
        if (index == 1 && routeInfo != null && displayRouteInfo)
            routeInfo.imageLoaded();
        else
            adapter.notifyDataSetChanged();
    }

    private void deleteSelectedRoute(int id) {

        ProjectAR.getInstance().routes.remove(selectedRoute);
        String[] rutas = new File(ResourceManager.getInstance().getRootPath()
                + "/routes").list();
        int i = 0;
        boolean cont = true;
        File f = null;

        // Borramos el xml
        while (cont && i < rutas.length) {

            Route ruta = ResourceManager.getInstance().getXmlManager()
                    .readRoute(rutas[i].split(".xml")[0]);
            if (ruta.id == id) {
                cont = false;
                f = new File(ResourceManager.getInstance().getRootPath()
                        + "/routes/" + rutas[i]);
            } else
                i++;
        }
        f.delete();

        // Borramos sus archivos temporales de /tmp
        File icon = new File(ResourceManager.getInstance().getRootPath()
                + "/tmp/route" + id + "icon.png");
        if (icon.exists())
            icon.delete();

        // borramos los archivos temporales de la carpeta de la propia ruta
        File dir = new File(ResourceManager.getInstance().getRootPath()
                + "/tmp/route" + id);
        if (dir.exists()) {
            ProjectAR.getInstance().cleanDir(dir.getPath());
            dir.delete();
        }
        ProjectAR.getInstance().LoadRoutes();
        loadRoutes(0);
    }

    public void AllAvailableRoutes() {
        this.allRoutesAvailables = ResourceManager.getInstance()
                .getXmlManager().readAvailableRoutes("allroutes.xml");
        File f = new File(ResourceManager.getInstance().getRootPath()
                + "/allroutes.xml");
        if (f.exists())
            f.delete();
        final String[] options = new String[allRoutesAvailables.size()];
        int i = 0;
        for (MinRouteInfo mri : allRoutesAvailables) {
            options[i] = mri.name;
            i++;
        }
        ProjectAR.getInstance().createDialog("Rutas disponibles", options,
                new SelectRouteToDownloadNotification(), null);
    }

    public ArrayList<MinRouteInfo> getAllRoutesAvailables() {
        return this.allRoutesAvailables;
    }

}

