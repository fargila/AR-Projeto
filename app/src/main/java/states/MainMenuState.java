package states;

import java.io.File;
import java.util.ArrayList;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import main.ProjectAR;
import main.PointOI;
import main.State;
import resources.ResourceManager;
import utils.NewRouteForm;
import utils.NewRouteGeoTaggingForm;
import utils.PointExtraInfoGeoTagging;
import utils.SavePointTemplate;

//import fraguel.android.notifications.UserOptionsTemplateNotification;
//import fraguel.android.resources.ResourceManager;


public class MainMenuState extends State {

    public static final int STATE_ID = 15;

    // Variables menu de opciones
    private static final int MENU_EXIT = 2;

    protected TextView gps;
    protected TextView orientation;
    private final CharSequence[] options = { "Plantilla en blanco",
            "Mediante captura de puntos" };

    // GeoTagging
    private ArrayList<PointOI> geoTaggingPoints;
    private String routeName;
    private NewRouteGeoTaggingForm f = null;
    private PointOI currentPoint = null;
    private PointExtraInfoGeoTagging extraInfo = null;
    private SavePointTemplate coordinatesCapturer = null;
    private String[] tempFiles = null;

    private NewRouteForm blankForm = null;

    public MainMenuState() {
        super();
        id = STATE_ID;
    }

    @Override
    public void load() {
        LayoutInflater li = ProjectAR.getInstance().getLayoutInflater();
        if (viewGroup == null)
            viewGroup = (ViewGroup) li.inflate(R.layout.mainmenu, null);

        FrameLayout title = (FrameLayout) viewGroup.getChildAt(0);
        FrameLayout btn_1 = (FrameLayout) viewGroup.getChildAt(1);
        FrameLayout btn_2 = (FrameLayout) viewGroup.getChildAt(2);
        FrameLayout btn_3 = (FrameLayout) viewGroup.getChildAt(3);
        FrameLayout btn_4 = (FrameLayout) viewGroup.getChildAt(4);

        AnimationSet setTitle = new AnimationSet(true);
        AnimationSet setBtn = new AnimationSet(true);

        Animation titleAnim = new AlphaAnimation(0.0f, 1.0f);
        titleAnim.setDuration(2000);

        Animation btnAnim1 = new AlphaAnimation(0.0f, 1.0f);
        btnAnim1.setDuration(1000);

        Animation btnAnim2 = new RotateAnimation(45, 0);
        btnAnim2.setDuration(1000);

        Animation btnAnim3 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -3.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        btnAnim3.setDuration(1000);

        setTitle.addAnimation(titleAnim);
        setBtn.addAnimation(btnAnim1);
        setBtn.addAnimation(btnAnim3);

        LayoutAnimationController controllerTitle = new LayoutAnimationController(
                setTitle, 0.25f);
        LayoutAnimationController controllerBtn = new LayoutAnimationController(
                setBtn, 0.25f);
        title.setLayoutAnimation(controllerTitle);
        btn_1.setLayoutAnimation(controllerBtn);
        btn_2.setLayoutAnimation(controllerBtn);
        btn_3.setLayoutAnimation(controllerBtn);
        btn_4.setLayoutAnimation(controllerBtn);

        ProjectAR.getInstance().addView(viewGroup);

        ((Button) ProjectAR.getInstance().findViewById(R.id.btn_freemode))
                .setOnClickListener((OnClickListener) ProjectAR.getInstance());
        ((Button) ProjectAR.getInstance().findViewById(R.id.btn_routemode))
                .setOnClickListener((OnClickListener) ProjectAR.getInstance());
        ((Button) ProjectAR.getInstance().findViewById(R.id.btn_interactivemode))
                .setOnClickListener((OnClickListener) ProjectAR.getInstance());
        ((Button) ProjectAR.getInstance().findViewById(R.id.btn_manager))
                .setOnClickListener((OnClickListener) ProjectAR.getInstance());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_freemode:
                ProjectAR.getInstance().changeState(MapState.STATE_ID);
                break;
            case R.id.btn_routemode:
                ProjectAR.getInstance().changeState(RouteManagerState.STATE_ID);
                break;
            case R.id.btn_interactivemode:
                ProjectAR.getInstance().cleanCache();
                Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                        "Caché borrada con éxito", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_manager:
                String[] allFiles = new File(ResourceManager.getInstance()
                        .getRootPath() + "/user").list();
                ArrayList<String> aux = new ArrayList<String>();
                for (String s : allFiles) {
                    if (s.endsWith(".tmp")) {
                        aux.add(s);
                    }
                }
                int i = 0;
                tempFiles = new String[aux.size()];
                for (String s : aux) {
                    tempFiles[i] = s;
                    i++;
                }

                ProjectAR.getInstance().createDialog("Crear ruta", options,
                        new UserOptionsTemplateNotification(), null);
                break;

            default:
                System.exit(0);
        }
    }

    @Override
    public Menu onCreateStateOptionsMenu(Menu menu) {

        menu.clear();
        // Menu de opciones creado por defecto
        menu.add(0, MENU_EXIT, 0, R.string.menu_exit).setIcon(
                R.drawable.ic_menu_exit);

        return menu;
    }

    @Override
    public boolean onStateOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_EXIT:
                System.exit(0);
                return true;
        }
        return false;
    }

    public NewRouteForm getBlankForm() {
        return blankForm;
    }

    public void setBlankForm(NewRouteForm f) {
        blankForm = f;
    }

    public void setGeoTaggingPoints(ArrayList<PointOI> geoTaggingPoints) {
        this.geoTaggingPoints = geoTaggingPoints;
    }

    public ArrayList<PointOI> getGeoTaggingPoints() {
        return geoTaggingPoints;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setGeoTaggingForm(NewRouteGeoTaggingForm f) {
        this.f = f;
    }

    public NewRouteGeoTaggingForm getGeoTaggingForm() {
        return f;
    }

    public void setCurrentPoint(PointOI currentPoint) {
        this.currentPoint = currentPoint;
    }

    public PointOI getCurrentPoint() {
        return currentPoint;
    }

    public void setExtraInfo(PointExtraInfoGeoTagging extraInfo) {
        this.extraInfo = extraInfo;
    }

    public PointExtraInfoGeoTagging getExtraInfo() {
        return extraInfo;
    }

    public SavePointTemplate getCoordinatesCapturer() {
        return coordinatesCapturer;
    }

    public void setCoordinatesCapturer(SavePointTemplate capturer) {
        this.coordinatesCapturer = capturer;
    }

    public void NewLocation(Location location) {
        if (coordinatesCapturer != null) {
            coordinatesCapturer.setLatitude((float) location.getLatitude());
            coordinatesCapturer.setLongitude((float) location.getLongitude());
        }
    }

    public String[] getTempFiles() {
        return tempFiles;
    }

}
