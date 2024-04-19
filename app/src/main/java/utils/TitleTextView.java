package utils;

import main.ProjectAR;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import com.example.arproject.R;

public class TitleTextView extends AutomaticScrollTextView {
    public static final int HEIGHT = 30;

    public TitleTextView(Context context) {
        super(context);
        this.setGravity(Gravity.CENTER_HORIZONTAL);
        this.setTextAppearance(ProjectAR.getInstance().getApplicationContext(),
                R.style.StateTitle);
        this.setBackgroundColor(Color.BLACK);
        this.setHeight(HEIGHT);
        this.setMinHeight(HEIGHT);
        this.setMaxHeight(HEIGHT);

    }

}
