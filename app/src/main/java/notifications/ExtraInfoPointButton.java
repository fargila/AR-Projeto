package notifications;

import android.content.DialogInterface;
import android.widget.Toast;
import main.ProjectAR;
import main.PointOI;
import states.MainMenuState;
import utils.SavePointTemplate;


public class ExtraInfoPointButton implements DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface arg0, int arg1) {
        MainMenuState state = (MainMenuState) ProjectAR.getInstance()
                .getCurrentState();
        PointOI point = state.getCurrentPoint();
        point.title = state.getExtraInfo().getPointName();
        state.getGeoTaggingPoints().add(point);
        Toast.makeText(
                ProjectAR.getInstance().getApplicationContext(),
                "Hay " + state.getGeoTaggingPoints().size()
                        + " puntos guardados", Toast.LENGTH_LONG).show();
        arg0.dismiss();
        state.setCoordinatesCapturer(new SavePointTemplate(FRAGUEL
                .getInstance().getApplicationContext()));
        ProjectAR.getInstance().createCustomDialog(
                state.getRouteName() + ": captura de coordenadas",
                state.getCoordinatesCapturer(), new CaptureCoordinatesButton(),
                "Capturar", new EndCaptureButton(), "Finalizar", null);
    }

}
