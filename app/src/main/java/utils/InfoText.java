package utils;

import fraguel.android.R;
import main.ProjectAR;

import android.content.Context;

public class InfoText extends androidx.appcompat.widget.AppCompatTextView {

    public InfoText(Context context) {
        super(context);
        this.setTextAppearance(ProjectAR.getInstance().getApplicationContext(), R.style.ScrollText);
    }

}
