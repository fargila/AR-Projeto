package utils;

import main.ProjectAR;
import states.MainMenuState;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PointExtraInfoGeoTagging extends LinearLayout {
    private final TextView pointName;

    @SuppressLint("SetTextI18n")
    public PointExtraInfoGeoTagging(Context context) {
        super(context);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        this.setOrientation(LinearLayout.VERTICAL);

        TextView nameLabel = new TextView(context);
        nameLabel.setText("Nombre del punto capturado:");
        nameLabel.setPadding(5, 5, 5, 5);
        this.addView(nameLabel);
        pointName = new EditText(context);
        pointName.setPadding(5, 0, 5, 0);

        MainMenuState state = (MainMenuState) ProjectAR.getInstance()
                .getCurrentState();

        pointName.setText("nombre" + state.getGeoTaggingPoints().size());
        this.addView(pointName);
    }

    public String getPointName() {
        return pointName.getText().toString();
    }
}
