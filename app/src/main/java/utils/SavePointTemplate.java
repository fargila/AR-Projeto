package utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SavePointTemplate extends LinearLayout {

    private final TextView latitudeGPS, longitudeGPS;

    @SuppressLint("SetTextI18n")
    public SavePointTemplate(Context context) {
        super(context);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        this.setOrientation(LinearLayout.VERTICAL);

        TextView label = new TextView(context);
        label.setText("Coordenadas GPS");
        label.setGravity(Gravity.CENTER_HORIZONTAL);
        this.addView(label);

        LinearLayout layoutLatitude = new LinearLayout(context);
        layoutLatitude.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layoutLatitude.setOrientation(LinearLayout.HORIZONTAL);
        layoutLatitude.setPadding(10, 10, 0, 0);

        TextView latitude = new TextView(context);
        latitude.setText("Latitude: ");
        latitudeGPS = new TextView(context);
        latitudeGPS.setText("0");

        layoutLatitude.addView(latitude);
        layoutLatitude.addView(latitudeGPS);
        this.addView(layoutLatitude);

        LinearLayout layoutLongitude = new LinearLayout(context);
        layoutLongitude.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layoutLongitude.setOrientation(LinearLayout.HORIZONTAL);
        layoutLongitude.setPadding(10, 10, 0, 0);

        TextView longitude = new TextView(context);
        longitude.setText("Longitud: ");
        longitudeGPS = new TextView(context);
        longitudeGPS.setText("0");

        layoutLongitude.addView(longitude);
        layoutLongitude.addView(longitudeGPS);
        this.addView(layoutLongitude);
    }

    @SuppressLint("SetTextI18n")
    public void setLatitude(float latitude) {
        latitudeGPS.setText(Float.toString(latitude));
    }
    @SuppressLint("SetTextI18n")
    public void setLongitude(float longitude) {
        longitudeGPS.setText(Float.toString(longitude));
    }
    public float getLatitude() {
        return Float.parseFloat(latitudeGPS.getText().toString());
    }
    public float getLontitude() {
        return Float.parseFloat(longitudeGPS.getText().toString());
    }

}
