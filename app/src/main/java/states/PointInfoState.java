package states;

import java.io.File;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import main.*;
import utils.TitleTextView;
import threads.ImageDownloadingThread;
import resources.ResourceManager;
import lists.InfoPointAdapter;
//import fraguel.android.R;


public class PointInfoState extends State {
    public static final int STATE_ID = 20;
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private GridView gridView;
    private TitleTextView title;
    private ImageView image;
    private TextView text;
    private boolean talk = true;
    private static final int MENU_POINTINFO_STARTTALK = 0;
    private static final int MENU_POINTINFO_STOPTALK = 1;
    private static final int MENU_POINTINFO_MAINMENU = 2;
    private static final int MENU_POINTINFO_BACK = 3;

    public PointInfoState() {
        super();
        id = STATE_ID;
    }

    @Override
    public void load() {
        ProjectAR.getInstance().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LinearLayout container = new LinearLayout(ProjectAR.getInstance()
                .getApplicationContext());
        container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        container.setOrientation(LinearLayout.VERTICAL);

        Display display = ((WindowManager) ProjectAR.getInstance()
                .getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();

        int heightAvailable = height - 2 * TitleTextView.HEIGHT;

        heightAvailable = heightAvailable / 2;

        title = new TitleTextView(ProjectAR.getInstance().getApplicationContext());
        container.addView(title);

        image = new ImageView(ProjectAR.getInstance().getApplicationContext());
        image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                heightAvailable));

        image.setPadding(10, 10, 10, 10);
        image.setAdjustViewBounds(true);

        container.addView(image);

        ScrollView sv = new ScrollView(ProjectAR.getInstance()
                .getApplicationContext());
        sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                heightAvailable - 40));
        text = new TextView(ProjectAR.getInstance().getApplicationContext());
        sv.addView(text);
        container.addView(sv);

        gridView = new GridView(ProjectAR.getInstance().getApplicationContext());
        gridView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        gridView.setNumColumns(3);
        gridView.setColumnWidth(width / 3);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setAdapter(new InfoPointAdapter(ProjectAR.getInstance()
                .getApplicationContext()));
        gridView.setScrollContainer(false);
        setGridViewListener();
        container.addView(gridView);

        viewGroup = container;
        ProjectAR.getInstance().addView(viewGroup);

        if (route != null && point != null) {
            talk = false;
            this.loadData(route, point);
        }
        talk = true;
    }

    public void setGridViewListener() {

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                switch (position) {

                    case 0:
                        if (point.images != null && point.images.size() != 0) {
                            ProjectAR.getInstance().changeState(ImageState.STATE_ID);
                            ProjectAR.getInstance().getCurrentState()
                                    .loadData(route, point);
                        } else
                            Toast.makeText(
                                    ProjectAR.getInstance().getApplicationContext(),
                                    "Este punto no tiene imágenes disponibles",
                                    Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        if (point.video != null && !point.video.equals(""))
                            ProjectAR.getInstance().startActivity(
                                    new Intent(Intent.ACTION_VIEW, Uri
                                            .parse(point.video)));
                        else
                            Toast.makeText(
                                    ProjectAR.getInstance().getApplicationContext(),
                                    "Este punto no tiene video disponible",
                                    Toast.LENGTH_SHORT).show();
                        break;

                    case 2:

                        ProjectAR.getInstance().changeState(ARState.STATE_ID);
                        ProjectAR.getInstance().getCurrentState()
                                .loadData(route, point);
                        break;

                }
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void unload() {
        talk = true;
        ProjectAR.getInstance().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        if (MapState.getInstance().getMapView() != null)
            MapState.getInstance().getGPS().setDialogDisplayed(false);
        super.unload();
    }

    @Override
    public boolean loadData(Route route, PointOI point) {
        super.loadData(route, point);
        String[] url = { point.icon };
        String path = ResourceManager.getInstance().getRootPath()
                + "/tmp/route" + route.id + "/point" + point.id + "icon.png";
        File f = new File(path);
        if (f.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(path);
            image.setImageBitmap(bmp);
        } else {
            imageThread = new ImageDownloadingThread(url, "point" + point.id
                    + "icon", ResourceManager.getInstance().getRootPath()
                    + "/tmp/route" + route.id + "/");
            imageThread.start();
            image.setImageDrawable(ProjectAR.getInstance().getResources()
                    .getDrawable(R.drawable.loading));
        }
        String titleText;
        titleText = point.title + " (" + route.name + ")";
        title.setText(titleText);

        text.setText(point.pointdescription);
        if (talk) {
            ProjectAR.getInstance().talk(
                    point.title + " \n \n \n " + point.pointdescription);
            talk = false;
        }
        return true;

    }

    @Override
    public void imageLoaded(int index) {
        if (index == 0) {
            String path = ResourceManager.getInstance().getRootPath()
                    + "/tmp/route" + route.id + "/point" + point.id + "icon"
                    + ".png";
            Bitmap bmp = BitmapFactory.decodeFile(path);
            image.setImageBitmap(bmp);
            image.invalidate();
        }

    }

    @Override
    public Menu onCreateStateOptionsMenu(Menu menu) {
        menu.clear();

        menu.add(0, MENU_POINTINFO_STOPTALK, 0, "Detener audio").setIcon(
                R.drawable.ic_menu_talkstop);
        menu.add(0, MENU_POINTINFO_STARTTALK, 0, "Comenzar audio").setIcon(
                R.drawable.ic_menu_talkplay);

        menu.add(0, MENU_POINTINFO_MAINMENU, 0, "Menu principal").setIcon(
                R.drawable.ic_menu_home);
        menu.add(0, MENU_POINTINFO_BACK, 0, "Atrás").setIcon(
                R.drawable.ic_menu_back);

        return menu;
    }

    @Override
    public boolean onStateOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_POINTINFO_STOPTALK:
                ProjectAR.getInstance().stopTalking();
                return true;
            case MENU_POINTINFO_STARTTALK:
                ProjectAR.getInstance().talk(
                        point.title + " \n \n \n " + point.pointdescription);
                return true;
            case MENU_POINTINFO_MAINMENU:
                ProjectAR.getInstance().changeState(MainMenuState.STATE_ID);
                return true;
            case MENU_POINTINFO_BACK:
                ProjectAR.getInstance().returnState();
                return true;
        }
        return false;
    }

}
