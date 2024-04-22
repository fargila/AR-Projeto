package GPS;

import android.location.Location;
import android.media.MediaPlayer;
import android.os.Vibrator;

import main.PointOI;
import com.example.arproject.R;

import main.ProjectAR;
import main.Route;

import java.util.ArrayList;

public class gpsProximity {
    public static final float proximityAlertDistance = 50;
    public static final float proximityAlertError = 30;
    protected float distance;
    protected Route currentRoute = null;
    protected PointOI currentPoint = null;
    protected float[] results = new float[3];
    protected String msg;
    protected float lastFix;
    protected double latitude=0, longitude=0, altitude=0;
    protected boolean hasBeenVisited;
    protected ArrayList<PointOI> pointsVisited;
    private Vibrator v;
    private MediaPlayer soundClip;

    public void GPSProximity(){
        v = (Vibrator) ProjectAR.getInstance().
                getSystemService(ProjectAR.getInstace().
                        getApplicationContext().VIBRATOR_SERVICE);

        soundClip = MediaPlayer.create(ProjectAR.getInstace().
                getApplicationContext(), R.raw.notification_sound);

        pointsVisited = new ArrayList<PointOI>();
        lastFix = 0;
    }

    public void onLocationChanged(Location location) {

    }
    public void mediaNotification(){
        v.vibrate(1000);
        soundClip.start();
    }

    public Route getCurrentRoute(){ return currentRoute; }
    public PointOI getCurrentPoint() { return currentPoint; }
}
