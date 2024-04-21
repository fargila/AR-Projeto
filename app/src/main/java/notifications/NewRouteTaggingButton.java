package notifications;

import java.util.ArrayList;

import android.content.DialogInterface;
import main.ProjectAR;
import main.PointOI;
import states.MainMenuState;
import utils.SavePointTemplate;

public class NewRouteTaggingButton implements DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        MainMenuState state = (MainMenuState) ProjectAR.getInstance()
                .getCurrentState();
        state.setRouteName(state.getGeoTaggingForm().getRouteName());
        state.setGeoTaggingPoints(new ArrayList<PointOI>());

        SavePointTemplate capturer = state.getCoordinatesCapturer();

        if (capturer == null) {
            capturer = new SavePointTemplate(ProjectAR.getInstance()
                    .getApplicationContext());
            state.setCoordinatesCapturer(capturer);
        }
        FRAGUEL.getInstance().createCustomDialog(
                state.getRouteName() + ": captura de coordenadas", capturer,
                new CaptureCoordinatesButton(), "Capturar",
                new EndCaptureButton(), "Finalizar", null);
        dialog.dismiss();
    }

}
