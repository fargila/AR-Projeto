package AR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;

import AR.core.Renderer;
import AR.core.TextureManager;

public class Shared {
    @SuppressLint("StaticFieldLeak")
    private static Context _context;
    private static Renderer _renderer;
    private static TextureManager _textureManager;

    public static Context context() {
        return _context;
    }

    public static void context(Context $c) {
        _context = $c;
    }

    public static Renderer renderer() {
        return _renderer;
    }

    public static void renderer(Renderer $r) {
        _renderer = $r;
    }

    /**
     * You must access the TextureManager instance through this accessor
     */
    public static TextureManager textureManager() {
        return _textureManager;
    }

    public static void textureManager(TextureManager $bm) {
        _textureManager = $bm;
    }
}
