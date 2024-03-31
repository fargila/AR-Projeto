package AR;

import android.graphics.Bitmap;

import androidx.appcompat.widget.ResourceManagerInternal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Utils {

    public static final float DEG = (float) (Math.PI / 180f);
    private static final int BYTES_PER_FLOAT = 4;

    public static Bitmap makeBitmapFromResourceId(String file){
        InputStream is;
        try{
            is = new FileInputStream(ResourceManager.getInstance()).getRootPath() + "" + file);

        }
        catch (FileNotFoundException e){
            return null;
        }
        finally{
            try{ is.close(); }
            catch (IOException ignored){}
        }
        return bitmap;
    }

    public static FloatBuffer makeFloatBuffer3(float $a, float $b, float $c){
        ByteBuffer b = ByteBuffer.allocateDirect(3*BYTES_PER_FLOAT);
        b.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = b.asFloatBuffer();
        buffer.put($a);
        buffer.put($b);
        buffer.put($c);
        buffer.position(0);

        return buffer;
    }

    public static FloatBuffer makeFloatBuffer4(float $a, float $b, float $c, float $d){
        ByteBuffer b = ByteBuffer.allocateDirect(4*BYTES_PER_FLOAT);
        b.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = b.asFloatBuffer();
        buffer.put($a);
        buffer.put($b);
        buffer.put($c);
        buffer.put($d);
        buffer.position(0);

        return buffer;
    }
}
