package utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import utils.NumberPicker;

public class NewRouteForm extends LinearLayout {

    private final EditText fileName, routeName;
    private final NumberPicker decenas, unidades;

    @SuppressLint("SetTextI18n")
    public NewRouteForm(Context context) {
        super(context);

        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        ScrollView sv = new ScrollView(context);
        sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        sv.setVerticalScrollBarEnabled(false);
        sv.setScrollBarStyle(ScrollView.SCROLLBARS_INSIDE_INSET);

        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        container.setOrientation(LinearLayout.VERTICAL);

        TextView fileLabel = new TextView(context);
        fileLabel.setText("");
        fileLabel.setPadding(5, 5, 5, 5);
        container.addView(fileLabel);
        fileName = new EditText(context);
        fileName.setPadding(5, 0, 5, 0);
        fileName.setText("rota1");
        container.addView(fileName);

        TextView nameLabel = new TextView(context);
        nameLabel.setText("Nome da rota:");
        nameLabel.setPadding(5, 5, 5, 5);
        container.addView(nameLabel);
        routeName = new EditText(context);
        routeName.setPadding(5, 0, 5, 0);
        routeName.setText("nome1");
        container.addView(routeName);

        TextView pointLabel = new TextView(context);
        pointLabel.setText("Número de POIs:");
        pointLabel.setPadding(5, 5, 5, 5);
        container.addView(pointLabel);

        LinearLayout numbers = new LinearLayout(context);
        numbers.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        numbers.setOrientation(LinearLayout.HORIZONTAL);

        decenas = new NumberPicker(context, null);
        decenas.setOrientation(LinearLayout.VERTICAL);
        numbers.addView(decenas);
        unidades = new NumberPicker(context, null);
        unidades.setOrientation(NumberPicker.VERTICAL);
        unidades.setValue(1);
        numbers.addView(unidades);

        numbers.setGravity(Gravity.CENTER_HORIZONTAL);

        container.addView(numbers);
        sv.addView(container);
        this.addView(sv);
    }

    public String getFileName() {
        String s = fileName.getText().toString();
        return s;
    }

    public String getRouteName() {
        String s = routeName.getText().toString();
        return s;
    }

    public int getDecenas() {
        return decenas.getValue();
    }

    public int getUnidades() {
        return unidades.getValue();
    }

    public int getNumPoints() {
        return 10 * decenas.getValue() + unidades.getValue();
    }

}
