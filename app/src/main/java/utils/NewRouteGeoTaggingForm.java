package utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewRouteGeoTaggingForm extends LinearLayout {

    private final TextView routeName;

    public NewRouteGeoTaggingForm(Context context) {
        super(context);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.setOrientation(LinearLayout.VERTICAL);

        TextView nameLabel = new TextView(context);
        nameLabel.setText("Nome da rota:");
        nameLabel.setPadding(5, 5, 5, 5);
        this.addView(nameLabel);
        routeName = new EditText(context);
        routeName.setPadding(5, 0, 5, 0);
        routeName.setText("nome1");
        this.addView(routeName);
    }

    public String getRouteName() {
        return routeName.getText().toString();
    }

}
