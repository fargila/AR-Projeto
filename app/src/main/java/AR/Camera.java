package AR;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.arproject.Manifest;
import com.example.arproject.R;

import java.util.Collections;

public class Camera2Activity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private SurfaceHolder surfaceHolder;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        closeCamera();
    }

   private void openCamera(){
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try
        {
            String cameraId = manager.getCameraIdList()[0];
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, new CameraDevice.StateCallback()
            {
               @Override
               public void onOpened(@NonNull CameraDevice camera){
                   cameraDevice = camera;
                   createCameraPreview();
               }
                @Override
                public void onDisconnected(@NonNull CameraDevice camera){
                    cameraDevice.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    cameraDevice.close();
                    cameraDevice = null;
                }

            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
   }

   private void createCameraPreview(){
        try {
            Surface surface = surfaceHolder.getSurface();
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public  void onConfigured(@NonNull CameraCaptureSession session){
                    if (cameraDevice == null)
                        return;

                    cameraCaptureSession = session;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session){
                    Log.e("Camera2Activity", "Erro, algo aconteceu na hora de configurar a câmera");
                }
            }, null);
        }
        catch (CameraAccessException e){
            e.printStackTrace();
        }
   }

   private void updatePreview(){
        if(cameraDevice == null)
            return;
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                }

                @Override
                public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);
                }
            }, backgroundHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
   }

   private  void closeCamera(){
        if (cameraCaptureSession != null){
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
        if(cameraDevice != null){
            cameraDevice.close();
            cameraDevice = null;
        }
   }

   @Override
    protected void onResume(){
        super.onResume();
        startBackgroundThread();
        if(surfaceView.isActivated()){
            openCamera();
        }
   }
    @Override
    protected void onPause(){
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

    }

    private  void stopBackgroundThread(){
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
