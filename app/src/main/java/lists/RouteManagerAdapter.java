package lists;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import main.ProjectAR;
//import fraguel.android.R;

public class RouteManagerAdapter extends BaseAdapter {

    private Context mContext;
    private Integer[] mImageIds = { R.drawable.galeria_fotos,
            R.drawable.galeria_video, R.drawable.realidad_aumentada, };

    public InfoPointAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return mImageIds.length;
    }
    @Override
    public Object getItem(int arg0) {
        return arg0;
    }
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View arg1, ViewGroup arg2) {
        Display display = ((WindowManager) ProjectAR.getInstance()
                .getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();

        ImageView imageView;
        imageView = new ImageView(mContext);
        imageView.setLayoutParams(new GridView.LayoutParams(width / 3, 50));
        imageView.setImageResource(mImageIds[position]);

        return imageView;

    }

}
