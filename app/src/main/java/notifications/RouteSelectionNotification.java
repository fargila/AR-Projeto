package notifications;

import android.content.DialogInterface;
import main.ProjectAR;
import main.PointOI;
import states.MapState;

public class RouteSelectionNotification implements
        DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        MapState.getInstance().setContextRoute(
                ProjectAR.getInstance().routes.get(which));

        final CharSequence[] options = new CharSequence[MapState.getInstance()
                .getContextRoute().pointsOI.size() + 1];

        options[0] = "Do início";
        int i = 1;
        for (PointOI p : MapState.getInstance().getContextRoute().pointsOI) {
            options[i] = p.title;
            i++;
        }
        dialog.dismiss();
        ProjectAR.getInstance().createDialog("Escolher um ponto inicial", options,
                new PointSelectionNotification(), new BackKeyNotification());

    }

}