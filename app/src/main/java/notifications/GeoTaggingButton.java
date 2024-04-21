package notifications;

import main.ProjectAR;
import resources.ResourceManager;
import states.MainMenuState;
import utils.NewRouteGeoTaggingForm;
import utils.SavePointTemplate;
import android.content.DialogInterface;

public class GeoTaggingButton implements DialogInterface.OnClickListener {

    private MainMenuState state = (MainMenuState) FRAGUEL.getInstance()
            .getCurrentState();

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 0:
                NewRouteGeoTaggingForm form = new NewRouteGeoTaggingForm(FRAGUEL
                        .getInstance().getApplicationContext());
                state.setGeoTaggingForm(form);
                FRAGUEL.getInstance().createCustomDialog(
                        "Nueva ruta mediante GeoTagging", form,
                        new NewRouteTaggingButton(), "Aceptar", null);
                break;
            default:
                String[] files = state.getTempFiles();
                String route = files[which - 1].split(".tmp")[0];
                ResourceManager.getInstance().fromTmpFile(
                        ResourceManager.getInstance().getRootPath() + "/user/"
                                + files[which - 1], route);
                SavePointTemplate capturer = state.getCoordinatesCapturer();

                capturer = new SavePointTemplate(FRAGUEL.getInstance()
                        .getApplicationContext());
                state.setCoordinatesCapturer(capturer);

                FRAGUEL.getInstance().createCustomDialog(
                        state.getRouteName() + ": captura de coordenadas",
                        capturer, new CaptureCoordinatesButton(), "Capturar",
                        new EndCaptureButton(), "Finalizar", null);
                break;

        }
        dialog.dismiss();
    }

}
