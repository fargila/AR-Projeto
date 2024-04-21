package AR;

import java.io.File;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import AR.vos.Light;
import main.ProjectAR;
import AR.core.Object3dContainer;
import AR.core.Scene;
import AR.interfaces.ISceneController;
import GPS.LatLon2UTM;
import resources.ResourceManager;
import states.ARState;


public class GLView extends GLSurfaceView implements ISceneController, Camera.PreviewCallback {

    public Scene scene;
    protected GLSurfaceView _glSurfaceView;

    protected Handler _initSceneHander;
    protected Handler _updateSceneHander;

    Object3dContainer _cube;

    final Runnable _initSceneRunnable = new Runnable() {
        public void run() {
            onInitScene();
        }
    };

    final Runnable _updateSceneRunnable = new Runnable() {
        public void run() {
            onUpdateScene();
        }
    };

    public GLView(Context context) {
        super(context);

        _initSceneHander = new Handler();
        _updateSceneHander = new Handler();

        //
        // These 4 lines are important.
        //
        Shared.context(context);
        scene = new Scene(this);
        fraguel.android.ar.core.Renderer r = new fraguel.android.ar.core.Renderer(scene);
        Shared.renderer(r);

        _glSurfaceView = this;

        _glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        _glSurfaceView.setRenderer(r);
        _glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        _glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    /**
     * Instantiation of Object3D's, setting their properties, and adding
     * Object3D's to the scene should be done here. Or any point thereafter.
     *
     * Note that this method is always called after GLCanvas is created, which
     * occurs not only on Activity.onCreate(), but on Activity.onResume() as
     * well. It is the user's responsibility to build the logic to restore state
     * on-resume.
     */
    public void initScene() {
        scene.reset();

        scene.backgroundTransparent(true);

        scene.lights().add(new Light());

        scene.camera().rotation.x = 0;
        scene.camera().rotation.y = 0;
        scene.camera().rotation.z = 0;
        scene.camera().position.x = 0;
        scene.camera().position.y = 0;
        scene.camera().position.z = 0;
        Log.d("FRAGUEL", "End AR load");
    }

    /**
     * All manipulation of scene and Object3D instance properties should go
     * here. Gets called on every frame, right before drawing.
     */
    public void updateScene() {
    }

    /**
     * Called _after_ scene init (ie, after initScene). Unlike initScene(), is
     * thread-safe.
     */
    public void onInitScene() {
        ARState arState= (ARState)FRAGUEL.getInstance().getCurrentState();
        LatLon2UTM ll = new LatLon2UTM();
        ll.setVariables(arState.getPointOI().arCoords[0], arState.getPointOI().arCoords[1]);
        float x = -(float) ll.getEasting();
        float y = 0;//arState.getPointOI().arCoords[2];
        float z = -(float) ll.getNorthing(arState.getPointOI().arCoords[0]);
        if (arState.getPointOI().urlfilesAr!=null){
            Toast.makeText(FRAGUEL.getInstance().getApplicationContext(), "Cargando Realidad Aumentada...", Toast.LENGTH_LONG).show();

            for (String s: arState.getPointOI().urlfilesAr){
                File f = new File(ResourceManager.getInstance().getRootPath()+"/ar/"+s);
                if (!s.equals("") && f.exists()){
                    scene.loadObject(s,x,y,z);
                }
            }
        }
        Toast.makeText(FRAGUEL.getInstance().getApplicationContext(), "Sistema cargado", Toast.LENGTH_LONG).show();
        if (arState.getPointOI().textAr!=null && arState.getPointOI().textAr!="")
            FRAGUEL.getInstance().talk(arState.getPointOI().textAr);

    }

    /**
     * Called _after_ scene init (ie, after initScene). Unlike initScene(), is
     * thread-safe.
     */
    public void onUpdateScene() {
    }

    public Handler getInitSceneHandler() {
        return _initSceneHander;
    }

    public Handler getUpdateSceneHandler() {
        return _updateSceneHander;
    }

    public Runnable getInitSceneRunnable() {
        return _initSceneRunnable;
    }

    public Runnable getUpdateSceneRunnable() {
        return _updateSceneRunnable;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
    }
}
