package AR;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This class handles the camera. In particular, the method setPreviewCallback
 * is used to receive camera images. The camera images are not processed in this
 * class but delivered to the GLLayer.
 *
 * @author Niels
 *
 */
public class CamLayer extends SurfaceView implements SurfaceHolder.Callback,
        PreviewCallback {
    int previewWidth = 533;
    int previewHeight = 480;

    public static CamLayer instance;
    Camera mCamera;
    Context context;
    boolean paused = true;

    public GLView synchronCallback;

    public CamLayer(Context context, int previewWidth, int previewHeight) {
        super(context);
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;

        Log.i("CamLayer", "CamLayer(GLCamTest context)");
        this.context = context;
        instance = this;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        SurfaceHolder mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("CamLayer", "surfaceDestroyed");
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        synchronized (this) {
            try {
                paused = true;
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                }
            } catch (Exception e) {
                Log.e("CamLayer", e.getMessage());
            }
        }
    }

    public void pausePreview() {
        paused = true;
        mCamera.stopPreview();
    }

    public void restartPreview() {
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);
        paused = false;
    }

    public void onPause() {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
            }
        } catch (Exception e) {
            Log.e("CamLayer", e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.i("CamLayer", "surfaceChanged");

        synchronized (this) {
            paused = false;

            mCamera = Camera.open();

            Camera.Parameters p = mCamera.getParameters();
            p.setPreviewSize(previewWidth, previewHeight);
            p.setSceneMode(Camera.Parameters.SCENE_MODE_ACTION);
            p.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
            p.setAntibanding(Camera.Parameters.ANTIBANDING_60HZ);
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(p);

            p = mCamera.getParameters();
            Size size = p.getPictureSize();
            previewWidth = size.width;
            previewHeight = size.height;

            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                Log.e("Camera", "mCamera.setPreviewDisplay(holder);");
            }

            PixelFormat pf = new PixelFormat();
            PixelFormat.getPixelFormatInfo(p.getPreviewFormat(), pf);
            int bufSize = (previewWidth * previewHeight * pf.bitsPerPixel) / 8;

            // Must call this before calling addCallbackBuffer to get all the
            // reflection variables setup
            initForACB();

            // Add three buffers to the buffer queue. I re-queue them once they
            // are used in
            // onPreviewFrame, so we should not need many of them.
            byte[] buffer = new byte[bufSize];
            addCallbackBuffer(buffer);
            buffer = new byte[bufSize];
            addCallbackBuffer(buffer);
            buffer = new byte[bufSize];
            addCallbackBuffer(buffer);

            setPreviewCallbackWithBuffer();

            mCamera.startPreview();
        }
    }

    Date start;
    int fcount = 0;
    int frameId = 0;
