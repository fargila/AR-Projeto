package states;

import android.location.LocationManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

import main.ProjectAR;
import main.State;
//import fraguel.android.R;

import notifications.GPSNotificationButton;
import notifications.WarningNotificationButton;


public class IntroState extends State {

    public static final int STATE_ID = 0;

    public IntroState() {
        super();
        id = STATE_ID;
    }

    @Override
    public void load() {

        LayoutInflater li = ProjectAR.getInstance().getLayoutInflater();
        if (viewGroup == null)
            viewGroup = (ViewGroup) li.inflate(R.layout.intro, null);
        viewGroup.setBackgroundResource(R.drawable.white);
        AnimationSet set = new AnimationSet(true);
        viewGroup.setKeepScreenOn(true);

        // Mueve la View
        Animation animation3 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -5.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation3.setDuration(2000);

        // Mueve la View
        Animation animation2 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                5.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation2.setDuration(2000);
        animation2.setStartOffset(3000);

        set.addAnimation(animation3);
        set.addAnimation(animation2);

        LayoutAnimationController controller = new LayoutAnimationController(
                set, 0.25f);
        viewGroup.setLayoutAnimation(controller);

        ProjectAR.getInstance().addView(viewGroup);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                ProjectAR.getInstance().changeState(MainMenuState.STATE_ID);
            }
        }, 5000);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public Menu onCreateStateOptionsMenu(Menu menu) {
        menu.clear();
        return menu;
    }

    @Override
    public boolean onStateOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void unload() {
        viewGroup.setKeepScreenOn(false);
        super.unload();
        // comprobamos si tiene activado el GPS
        if (!ProjectAR.getInstance().getLocationManager()
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ProjectAR.getInstance().createTwoButtonNotification(
                    R.string.title_no_gps_spanish, R.string.alert_gps_spanish,
                    R.string.gps_enable_button_spanish,
                    R.string.gps_cancel_button_spanish,
                    new GPSNotificationButton(),
                    new WarningNotificationButton());
        }

    }

}
