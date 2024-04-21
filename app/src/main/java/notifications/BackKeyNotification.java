package notifications;

import states.MapState;
import android.content.DialogInterface;
import android.view.KeyEvent;

public class BackKeyNotification implements DialogInterface.OnKeyListener {

    @Override
    public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
        if (arg2.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            MapState.getInstance().setContextMenuDisplayed(false);
            MapState.getInstance().setContextRoute(null);
            arg0.dismiss();
            return true;
        }
        return false;
    }

}

