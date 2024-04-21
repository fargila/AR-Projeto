package gallery;

import main.ProjectAR;
import states.ImageState;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.Gallery;

public class FullScreenGallery extends Gallery{

    private boolean orientationChanged;
    public FullScreenGallery(Context context) {
        super(context);
        orientationChanged=false;
    }
    @Override
    public void draw(Canvas canvas) {
        if (orientationChanged){
            int i=this.getSelectedItemPosition();
            orientationChanged=false;
            super.draw(canvas);
            this.setSelection(0,true);
            this.setSelection(this.getCount()-1,true);
            this.setSelection(i,true);
            ((ImageState) ProjectAR.getInstance().getCurrentState()).changeOrientationFinished();
        }else
            super.draw(canvas);


    }
    public void setOrientationChanged(boolean b){
        orientationChanged=b;
    }

}
