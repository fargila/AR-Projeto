package main;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import threads.ImageDownloadingThread;
import states.MapState;

public abstract class State implements Comparable<State> {

    protected int id;
    protected ViewGroup viewGroup;
    protected PointOI point = null;
    protected Route route = null;
    protected ImageDownloadingThread imageThread = null;

    public State() {
        super();
    }

    public abstract void load();

    public void unload() {
        ProjectAR.getInstance().stopTalking();
        ProjectAR.getInstance().removeAllViews();
    }

    @Override
    public int compareTo(State another) {
        if (id == another.id)
            return 0;
        return 1;
    }

    public int getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public PointOI getPointOI() {
        return point;
    }

    public ImageDownloadingThread getImageThread() {
        return imageThread;
    }

    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    // estos dos métodos son para probar los datos de los sensores

    public boolean onConfigurationChanged(Configuration newConfig) {
        return false;
    }

    /**
     * Method which provides the three rotation values in degrees respect to the
     * global axis.
     *
     * @param values
     *            values[0]: azimuth, rotation around the Z axis. values[1]:
     *            pitch, rotation around the X axis. values[2]: roll, rotation
     *            around the Y axis.
     */
    public void onRotationChanged(float[] values) {
    }

    /**
     * Method which provides the current location using the GPS.
     *
     * @param values
     *            values[0]: Latitude. values[1]: Longitude. values[2]:
     *            Altitude.
     */
    public void onLocationChanged(float[] values) {
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        MapState.getInstance().setContextMenuDisplayed(false);
        ProjectAR.getInstance().closeContextMenu();
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK & event.getAction() == KeyEvent.ACTION_DOWN)
        {
            MapState.getInstance().setContextMenuDisplayed(false);
            ProjectAR.getInstance().closeContextMenu();
            ProjectAR.getInstance().returnState();
            ProjectAR.getInstance().stopTalking();
            return true;
        } else
            return false;
    }

    public abstract void onClick(View v);
    // Métodos para modificar menu de opciones y sus eventos desde un estado
    public abstract Menu onCreateStateOptionsMenu(Menu menu);
    public abstract boolean onStateOptionsItemSelected(MenuItem item);
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
    public void onUtteranceCompleted(String arg0) {
    }

    public boolean loadData(Route r, PointOI p) {
        route = r;
        point = p;
        return false;
    }

    public void imageLoaded(int index) {
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    }
    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }
}
