package states;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;

import java.util.ArrayList;
import java.util.List;

import main.PointOI;
import main.ProjectAR;
import main.Route;
import resources.ResourceManager;

public class MapState extends State implements View.OnTouchListener {

    // Singleton
    private static MapState mapInstance;
    private boolean isMyPosition;
    private MyPositionOverlay me = null;

    // Variables de los botones del men
    private static final int MAPSTATE_MENU_CHANGEMAP = 1;
    private static final int MAPSTATE_MENU_COMPASS = 4;
    private static final int MAPSTATE_MENU_STARTROUTE = 5;
    private static final int MAPSTATE_MENU_DRAWPATH = 6;
    private static final int MAPSTATE_MENU_STOPTALKING = 7;
    private static final int MAPSTATE_MENU_STOPROUTE = 8;

    public static final int STATE_ID = 2;

    private MapController mapControl;
    private MapView mapView;
    private View popupPI;
    private View popupPIonroute;
    private View popupOnRoute;
    private View returnToMyPosition;
    private List<Overlay> mapOverlays;
    private boolean isPopupPI;
    private boolean isPopupOnRoute;
    private boolean isPopupPIOnRoute;
    private boolean isContextMenuDisplayed, showWay = true;
    private Route routeContext;
    public final CharSequence[] options = { "Desde el principio", "",
            "Elegir otra ruta" };
    private final CharSequence[] end = { "Abandonar ruta" };
    public final CharSequence[] rutas = new CharSequence[ProjectAR.getInstance().routes.size()];
    private RouteInfoDialog dialog;
    private RouteOverlay routeOverlay = null;
    private NextPointOverlay nextPointOverlay = null;

    public MapState() {
        super();
        id = STATE_ID;
        // Singleton
        mapInstance = this;

    }

    public static MapState getInstance() {
        if (mapInstance == null)
            mapInstance = new MapState();
        return mapInstance;
    }

    @Override
    public void load() {
        // Creamos e importamos el layout del xml
        LayoutInflater li = ProjectAR.getInstance().getLayoutInflater();
        if (viewGroup == null)
            viewGroup = (ViewGroup) li.inflate(R.layout.maingooglemaps, null);
        ProjectAR.getInstance().addView(viewGroup);

        // Creamos e importamos el popup del xml
        isPopupPI = false;
        popupPI = li.inflate(R.layout.popup, null);
        // LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
        // LayoutParams.WRAP_CONTENT);
        // popupView.setLayoutParams(params);
        popupPI.setOnTouchListener((View.OnTouchListener) ProjectAR.getInstance());

        // Creamos e importamos el popup onroute del xml
        isPopupPIOnRoute = false;
        popupPIonroute = li.inflate(R.layout.popup2, null);
        popupPIonroute.setOnTouchListener((View.OnTouchListener) ProjectAR.getInstance());

        // Creamos e importamos el popup onroute del xml
        isPopupOnRoute = false;
        popupOnRoute = li.inflate(R.layout.popup3, null);
        popupOnRoute
                .setOnTouchListener((View.OnTouchListener) ProjectAR.getInstance());

        // Creamos e importamos el popup 'return to my position' del xml
        isMyPosition = true;
        returnToMyPosition = li.inflate(R.layout.tomyposition, null);
        returnToMyPosition.setOnTouchListener((View.OnTouchListener) ProjectAR
                .getInstance());
        returnToMyPosition.findViewById(R.id.btn_return_to_position)
                .setOnClickListener((View.OnClickListener) ProjectAR.getInstance());

        // Creamos, importamos y configuramos la mapview del xml
        mapView = (MapView) ProjectAR.getInstance().findViewById(R.id.mapview);
        if (me == null)
            me = new MyPositionOverlay(ProjectAR.getInstance().getApplicationContext(), mapView);
        mapView.setTraffic(false);
        mapView.setBuiltInZoomControls(true);
        mapView.setClickable(true);
        mapView.setEnabled(true);

        mapView.setOnTouchListener((View.OnTouchListener) ProjectAR.getInstance());

        mapControl = mapView.getController();
        GeoPoint pointInit = new GeoPoint((int) (40.4435602 * 1000000),
                (int) (-3.7267881 * 1000000));
        mapControl.setZoom(15);

        if (me.getMyLocation() != null)
            mapControl.setCenter(me.getMyLocation());
        else
            mapControl.setCenter(pointInit);

        // Creamos los Overlays
        mapOverlays = mapView.getOverlays();

        // Cargamos todo
        if (!me.isRouteMode())
            this.reStartMap();
        else {
            mapControl.setCenter(me.getMyLocation());
            // refreshMapRouteMode();
        }

        isContextMenuDisplayed = false;
        routeContext = null;
        mapView.setKeepScreenOn(true);

    }

    @Override
    public void unload() {
        this.removeAllPopUps();
        mapView.removeView(this.returnToMyPosition);
        isMyPosition = true;
        super.unload();
        mapView.setKeepScreenOn(false);
    }

    public boolean isPopupPI() {
        return isPopupPI;
    }

    public boolean isPopUpPIOnRoute() {
        return isPopupPIOnRoute;
    }

    public boolean isPopupOnRoute() {
        return isPopupOnRoute;
    }

    public boolean isMyPosition() {
        return isMyPosition;
    }

    public boolean isAnyPopUp() {
        return isPopupPI || isPopupPIOnRoute || isPopupOnRoute;
    }

    public void setPopupPI() {
        ProjectAR.getInstance().addView(popupPI);
        this.isPopupPI = true;
    }

    public void setPopupPIOnRoute() {
        ProjectAR.getInstance().addView(popupPIonroute);
        this.isPopupPIOnRoute = true;
    }

    public void setPopupOnRoute() {
        ProjectAR.getInstance().addView(popupOnRoute);
        this.isPopupOnRoute = true;
    }

    public void setPopupMyPosition() {
        ProjectAR.getInstance().addView(returnToMyPosition);
        isMyPosition = false;
    }

    public void removePopUpMyPosition() {
        ProjectAR.getInstance().getView().removeView(returnToMyPosition);
        isMyPosition = true;
    }

    public void removePopUpPI() {
        ProjectAR.getInstance().getView().removeView(popupPI);
        this.isPopupPI = false;

    }

    public void removePopUpOnRoute() {
        ProjectAR.getInstance().getView().removeView(popupOnRoute);
        this.isPopupOnRoute = false;

    }

    public void removePopUpPIOnRoute() {
        ProjectAR.getInstance().getView().removeView(popupPIonroute);
        this.isPopupPIOnRoute = false;

    }

    @Override
    public void onClick(View v) {

        this.removePopUpPI();
        this.removePopUpOnRoute();
        this.removePopUpPIOnRoute();

        switch (v.getId()) {
            case R.id.btn_popupPI_info:
                ProjectAR.getInstance().changeState(InfoState.STATE_ID);
                ProjectAR.getInstance().getCurrentState().loadData(route, point);
                break;
            case R.id.btn_popupPI_photo:
                if (point.images != null && point.images.size() != 0)
                {
                    ProjectAR.getInstance().changeState(ImageState.STATE_ID);
                    ProjectAR.getInstance().getCurrentState().loadData(route, point);
                }
                else
                    Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                            "Este punto no tiene imágenes disponibles",
                            Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_popupPI_video:
                if (point.video != null && !point.video.equals(""))
                    ProjectAR.getInstance().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(point.video)));
                else
                    Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                            "Este punto no tiene video disponible",
                            Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_popupPI_ar:
                ProjectAR.getInstance().changeState(ARState.STATE_ID);
                ProjectAR.getInstance().getCurrentState().loadData(route, point);
                break;
            case R.id.btn_popupPIonroute_moreinfo:
                ProjectAR.getInstance().changeState(PointInfoState.STATE_ID);
                ProjectAR.getInstance().getCurrentState().loadData(route, point);
                break;
            case R.id.btn_return_to_position:
                ProjectAR.getInstance().getView().removeView(returnToMyPosition);
                isMyPosition = true;
                mapControl.animateTo(getMyLocation());
                break;
            default:

        }

    }

    @Override
    public void imageLoaded(int index) {
        if (index == 0) {
            String path = ResourceManager.getInstance().getRootPath()
                    + "/tmp/route" + Integer.toString(route.id) + "/point"
                    + point.id + "icon.png";
            Bitmap bmp = BitmapFactory.decodeFile(path);
            ((ImageView) popupPI.findViewById(R.id.popupPI_imagen2))
                    .setImageBitmap(bmp);
            popupPI.invalidate();
        } else {
            setImageToDialog();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent mev) {
        if (view == popupPI && isPopupPI()) {
            removePopUpPI();

        }

        if (view == popupPIonroute && isPopUpPIOnRoute()) {
            removePopUpPIOnRoute();

        }
        if (view == popupOnRoute && isPopupOnRoute()) {
            removePopUpOnRoute();

        }
        return true;
    }

    public MapView getMapView() {
        return mapView;
    }

    public View getPopupPI() {
        return popupPI;
    }

    public View getPopupPIonroute() {
        return popupPIonroute;
    }

    public View getPopupOnRoute() {
        return popupOnRoute;
    }

    public void animateTo(GeoPoint g) {
        this.isMyPosition = false;
        mapControl.animateTo(g);
    }

    public void refreshMapRouteMode() {
        mapOverlays.clear();
        mapOverlays.add(me);
        // pintamos los puntos de la ruta hasta que hayamos calculado la ruta
        addRouteOverlays();
        routeOverlay = new RouteOverlay();
    }

    private void addRouteOverlays() {

        // pintamos los ya visitados
        MapItemizedOverlays visited = new MapItemizedOverlays(ProjectAR
                .getInstance().getResources()
                .getDrawable(R.drawable.map_marker_visited), ProjectAR.getInstance());

        for (PointOI point : me.getRoutePointsVisited()) {
            visited.addOverlay(new PointOverlay(new GeoPoint(
                    (int) (point.coords[0] * 1000000),
                    (int) (point.coords[1] * 1000000)), point.title,
                    point.title, point, me.getCurrentRoute()));
        }

        if (visited.size() != 0)
            mapOverlays.add(visited);

        // pintamos los no visitados
        visited = new MapItemizedOverlays(ProjectAR.getInstance().getResources()
                .getDrawable(R.drawable.map_marker_notvisited),
                ProjectAR.getInstance());
        for (PointOI point : me.getRoutePointsNotVisited()) {
            visited.addOverlay(new PointOverlay(new GeoPoint(
                    (int) (point.coords[0] * 1000000),
                    (int) (point.coords[1] * 1000000)), point.title,
                    point.title, point, me.getCurrentRoute()));
        }
        if (visited.size() != 0)
            mapOverlays.add(visited);

    }

    /**
     * ¡¡¡Warning!!! Only use it if the thread has finished, otherwise will give
     * you a Concurrent Exception
     */
    public void routeLoaded() {
        mapOverlays.clear();
        mapOverlays.add(me);
        // pintamos las líneas que conforman la ruta
        if (routeOverlay != null)
            mapOverlays.add(routeOverlay);
        // pintamos los puntos de la ruta
        addRouteOverlays();
    }

    public void startRoute() {
        this.removeAllPopUps();
        Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                "Calculando la ruta...", Toast.LENGTH_LONG).show();
        refreshMapRouteMode();
    }

    public void removeAllPopUps() {
        removePopUpPI();
        this.removePopUpOnRoute();
        this.removePopUpPIOnRoute();
    }

    public void reStartMap() {
        mapOverlays.clear();
        routeOverlay = null;
        loadAllPoints();
    }

    public void loadAllPoints() {
        // PUNTOS LEIDOS DE FICHERO
        Drawable image;
        MapItemizedOverlays capa;
        GeoPoint point;
        PointOverlay item;
        for (Route r : ProjectAR.getInstance().routes) {
            image = ProjectAR.getInstance().getResources()
                    .getDrawable(R.drawable.map_marker_notvisited);

            capa = new MapItemizedOverlays(image, ProjectAR.getInstance());
            for (PointOI p : r.pointsOI) {
                point = new GeoPoint((int) (p.coords[0] * 1000000),
                        (int) (p.coords[1] * 1000000));
                item = new PointOverlay(point, p.title, p.title, p, r);
                capa.addOverlay(item);
            }
            mapOverlays.add(capa);
        }
        mapOverlays.add(me);
    }

    public GeoPoint getMyLocation() {
        return me.getMyLocation();
    }

    @Override
    public Menu onCreateStateOptionsMenu(Menu menu) {

        // Borramos el menu de opciones anterior
        menu.clear();
        // Añadimos las opciones del menu
        if (me.isRouteMode())
            if (showWay)
                menu.add(0, MAPSTATE_MENU_DRAWPATH, 0, "¡Guíame!").setIcon(
                        R.drawable.ic_menu_guide);
            else
                menu.add(0, MAPSTATE_MENU_DRAWPATH, 0, "No guiar").setIcon(
                        R.drawable.ic_menu_noguide);
        if (ProjectAR.getInstance().isTalking())
            menu.add(0, MAPSTATE_MENU_STOPTALKING, 0, "Detener audio").setIcon(
                    R.drawable.ic_menu_talkstop);
        menu.add(0, MAPSTATE_MENU_CHANGEMAP, 0,
                R.string.mapstate_menu_changemap).setIcon(
                R.drawable.ic_menu_mapmode);
        menu.add(0, MAPSTATE_MENU_COMPASS, 0, R.string.mapstate_menu_compass)
                .setIcon(R.drawable.ic_menu_compass);
        if (!me.isRouteMode())
            menu.add(0, MAPSTATE_MENU_STARTROUTE, 0, "Comenzar ruta").setIcon(
                    R.drawable.ic_menu_startroute);
        else
            menu.add(0, MAPSTATE_MENU_STOPROUTE, 0, "Abandonar ruta").setIcon(
                    R.drawable.ic_menu_stoproute);

        return menu;
    }

    @Override
    public boolean onStateOptionsItemSelected(MenuItem item) {

        // Añadimos los eventos del menu de opciones
        switch (item.getItemId()) {

            case MAPSTATE_MENU_CHANGEMAP:
                if (mapView.isSatellite())
                    mapView.setSatellite(false);
                else
                    mapView.setSatellite(true);
                return true;

            case MAPSTATE_MENU_DRAWPATH:
                if (showWay) {
                    nextPointOverlay = new NextPointOverlay();
                    mapOverlays.add(nextPointOverlay);
                } else {
                    mapOverlays.remove(nextPointOverlay);
                }
                showWay = !showWay;
                mapView.invalidate();
                return true;

            case MAPSTATE_MENU_COMPASS:
                if (me.isCompassEnabled())
                    me.disableCompass();
                else
                    me.enableCompass();
                return true;

            case MAPSTATE_MENU_STARTROUTE:
                int i = 0;
                for (Route r : ProjectAR.getInstance().routes) {
                    rutas[i] = r.name;
                    i++;
                }

                ProjectAR.getInstance()
                        .createDialog("Elegir ruta", rutas,
                                new RouteSelectionNotification(),
                                new BackKeyNotification());

                return true;
            case MAPSTATE_MENU_STOPTALKING:
                ProjectAR.getInstance().stopTalking();
                return true;

            case MAPSTATE_MENU_STOPROUTE:
                ProjectAR.getInstance()
                        .createDialog("¿Desea abandonar la ruta?", end,
                                new StopRouteNotification(),
                                new BackKeyNotification());
                return true;
        }
        return false;
    }

    public void setContextMenuDisplayed(boolean isContextMenuDisplayed) {
        this.isContextMenuDisplayed = isContextMenuDisplayed;
    }

    public boolean isContextMenuDisplayed() {
        return isContextMenuDisplayed;
    }

    public Route getContextRoute() {
        return routeContext;
    }

    public void setContextRoute(Route r) {
        routeContext = r;
    }

    private void setImageToDialog() {
        if (dialog != null)
            dialog.imageLoaded();
    }

    public RouteInfoDialog getDialog() {
        return dialog;
    }

    public void setRouteInfoDialog(RouteInfoDialog d) {
        dialog = d;
    }

    public MyPositionOverlay getGPS() {
        return me;
    }

    // ****************************************************************************************
    // ****************************************************************************************
    public class MyPositionOverlay extends MyLocationOverlay {

        private GPSProximityRouteListener routeListener;
        private GPSProximityListener pointListener;
        private boolean isDialogDisplayed = false, routeMode = false;

        private float[] position = { 0, 0, 0 };

        public MyPositionOverlay(Context context, MapView mapView) {
            super(context, mapView);
            routeListener = new GPSProximityRouteListener();
            pointListener = new GPSProximityListener();
            this.disableCompass();
            this.enableMyLocation();

        }

        @Override
        public synchronized void onLocationChanged(Location location) {
            super.onLocationChanged(location);

            // notify changes to current state
            position[0] = (float) location.getLatitude();
            position[1] = (float) location.getLongitude();
            position[2] = (float) location.getAltitude();
            FRAGUEL.getInstance().getCurrentState().onLocationChanged(position);

            if (!routeMode)
                pointListener.onLocationChanged(location);
            else
                routeListener.onLocationChanged(location);

            if (isMyPosition)
                mapControl.animateTo(getMyLocation());
            if (FRAGUEL.getInstance().getCurrentState().getId() == MainMenuState.STATE_ID) {
                MainMenuState state = (MainMenuState) FRAGUEL.getInstance()
                        .getCurrentState();
                state.NewLocation(location);
            }

            // Añadir información de la posición a la matriz de rotación general
            // rotMatrix: matriz 4X4 de rotación para pasarla a OpenGL
            float[] rotMatrix = FRAGUEL.getInstance().getRotMatrix();
            rotMatrix[3] = position[0];
            rotMatrix[7] = position[1];
            rotMatrix[11] = position[2];
        }

        @Override
        public void onProviderDisabled(String provider) {
            super.onProviderDisabled(provider);
        }

        public double getLatitude() {
            return position[0];
        }

        public double getLongitude() {
            return position[1];
        }

        public double getAltitude() {
            return position[2];
        }

        public void setDialogDisplayed(boolean isDialogDisplayed) {
            this.isDialogDisplayed = isDialogDisplayed;
        }

        public boolean isDialogDisplayed() {
            return isDialogDisplayed;
        }

        public void startRoute(Route r, PointOI p) {
            routeMode = true;
            if (p == null)
                p = r.pointsOI.get(0);
            routeListener.startRoute(r, p);

        }

        public ArrayList<PointOI> getRoutePointsVisited() {
            if (routeMode == true)
                return routeListener.pointsVisited();
            else
                return null;

        }

        public ArrayList<PointOI> getRoutePointsNotVisited() {
            if (routeMode == true)
                return routeListener.pointsToVisit();
            else
                return null;
        }

        public void stopRoute() {
            removeAllPopUps();
            routeMode = false;
            MapState.getInstance().reStartMap();
        }

        public boolean isRouteMode() {
            return routeMode;
        }

        public Route getCurrentRoute() {
            return routeListener.getCurrentRoute();
        }

    }

}
