package gallery;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.example.arproject.R;

import resources.ResourceManager;
//import fraguel.android.R;
import main.ProjectAR;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    Bitmap bmp;

    private ArrayList<Pair<String, String>> urls = new ArrayList<Pair<String, String>>();

    public ImageAdapter(Context c) {
        mContext = c;
        TypedArray a = ProjectAR.getInstance().obtainStyledAttributes(
                R.styleable.HelloGallery);mGalleryItemBackground = a.getResourceId(
                R.styleable.HelloGallery_android_galleryItemBackground, 0);
        a.recycle();
    }

    public int getCount() {
        return urls.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<Pair<String, String>> data) {
        urls = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i;
        if (convertView == null) {
            i = new ImageView(mContext);

            i.setLayoutParams(new Gallery.LayoutParams(200, 150));
            i.setScaleType(ImageView.ScaleType.FIT_XY);
            i.setBackgroundResource(mGalleryItemBackground);

        } else {
            i = (ImageView) convertView;
        }

        String path = "";
        path = ResourceManager.getInstance().getRootPath()
                + "/tmp/"
                + "route"
                + Integer.toString(ProjectAR.getInstance().getCurrentState()
                .getRoute().id)
                + "/"
                + "point"
                + Integer.toString(ProjectAR.getInstance().getCurrentState()
                .getPointOI().id) + "images" + position + ".png";

        File f = new File(path);
        if (f.exists()) {
            bmp = BitmapFactory.decodeFile(path);
            i.setImageBitmap(bmp);
        } else {
            i.setImageDrawable(ProjectAR.getInstance().getResources()
                    .getDrawable(R.drawable.loading));
        }

        return i;
    }

    public void recycleBitmap() {
        if (bmp != null)
            bmp.recycle();
    }

}