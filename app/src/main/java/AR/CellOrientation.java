package AR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.example.arproject.R;

public class CellOrientation {

    private  CellOrientation cellOrientation;
    private SensorManager sensorManager;
    private Sensor sensorAcce;
    private Sensor sensorMagn;
    private SensorEventListener listener;
    private float[] matrix = new float[16];

    public void start(Context context){
        listener = new SensorEventListener() {
            private final float[] orientation = new float[3];
            private final float[] acceleration = new float[3];

            @Override
            public void onSensorChanged(SensorEvent event) {
                int type = event.sensor.getType();

                if(type == Sensor.TYPE_MAGNETIC_FIELD)
                {
                    orientation[0] = (orientation[0]*1 + event.values[0])*0.5f;
                    orientation[1] = (orientation[1]*1 + event.values[1])*0.5f;
                    orientation[2] = (orientation[2]*1 + event.values[2])*0.5f;
                }
                else if(type == Sensor.TYPE_ACCELEROMETER)
                {
                    acceleration[0] = (acceleration[0]*1 + event.values[0])*0.33334f;
                    acceleration[1] = (acceleration[1]*1 + event.values[1])*0.33334f;
                    acceleration[2] = (acceleration[2]*1 + event.values[2])*0.33334f;
                }
                if((type == Sensor.TYPE_MAGNETIC_FIELD) || (type == Sensor.TYPE_ACCELEROMETER)){
                    float[] newMatrix = new float[16];
                    SensorManager.getRotationMatrix(newMatrix, null, acceleration, orientation);
                    SensorManager.remapCoordinateSystem(newMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, newMatrix);
                    matrix = newMatrix;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorAcce = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);

        sensorManager.registerListener(listener, sensorAcce, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorMagn, SensorManager.SENSOR_DELAY_FASTEST);

    }
}
