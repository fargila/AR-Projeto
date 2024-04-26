package main;

import android.app.ProgressDialog;
import android.view.Menu;

import com.example.arproject.R;
import java.util.ArrayList;

public class Teste{

    // Routes and Points OI
    public ArrayList<Route> routes;
    public ArrayList<PointOI> pointsOI;

    // Menu variable buttons
    private static final int MENU_MAIN = 1;
    private static final int MENU_EXIT = 2;
    private ProgressDialog dialog;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        // Menu de opciones creado por defecto
        menu.add(0, MENU_MAIN, 0, R.string.menu_menu).setIcon(R.drawable.info);
        menu.add(0, MENU_EXIT, 0, R.string.menu_exit).setIcon(R.drawable.info);

        // Menu de opciones del estado
        menu = currentState.onCreateStateOptionsMenu(menu);

        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Menu de opciones del estado
        onCreateOptionsMenu(menu);

        return true;
    }
}