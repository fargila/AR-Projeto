package notifications;

import android.content.DialogInterface;
import main.ProjectAR;
import resources.ResourceManager;
import states.MainMenuState;

public class ExportPointsButton implements DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        MainMenuState state = (MainMenuState) ProjectAR.getInstance()
                .getCurrentState();
        switch (which) {
            case 0:
                ResourceManager.getInstance().toTempFile();
                break;
            case 1:
                ResourceManager.getInstance().createXMLFromPoints(
                        state.getRouteName(), state.getRouteName(), -1,
                        state.getGeoTaggingPoints());
                break;
        }
    }
}