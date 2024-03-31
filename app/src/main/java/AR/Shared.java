package AR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;

public class Shared {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static GLSurfaceView.Renderer renderer;
    private static TextureManager textureManager;
    private static Context getContext() {
        return context;
    }
    private static void setContext(Context c){
        context = c;
    }
    public static GLSurfaceView.Renderer getRenderer() {
        return  renderer;
    }
    public static void setRenderer(GLSurfaceView.Renderer r){
        renderer = r;
    }
    public static TextureManager getTextureManager(){
        return  textureManager;
    }
    public static void setTextureManager(TextureManager tm){
        textureManager = tm;
    }
}
