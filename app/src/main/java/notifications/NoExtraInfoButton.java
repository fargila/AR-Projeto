package notifications;


import main.ProjectAR;
import main.PointOI;
import states.MainMenuState;
import utils.SavePointTemplate;
import android.content.DialogInterface;
import android.widget.Toast;

public class NoExtraInfoButton implements DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface arg0, int arg1) {
        MainMenuState state = (MainMenuState) ProjectAR.getInstance()
                .getCurrentState();
        PointOI point = state.getCurrentPoint();
        state.getGeoTaggingPoints().add(point);
        Toast.makeText(
                ProjectAR.getInstance().getApplicationContext(),
                "Existem " + state.getGeoTaggingPoints().size()
                        + " pontos guardados", Toast.LENGTH_LONG).show();
        state.setCoordinatesCapturer(new SavePointTemplate(FRAGUEL
                .getInstance().getApplicationContext()));
        ProjectAR.getInstance().createCustomDialog(
                state.getGeoTaggingForm().getRouteName()
                        + ": captura das coordenadas",
                state.getCoordinatesCapturer(), new CaptureCoordinatesButton(),
                "Capturar", new EndCaptureButton(), "Finalizar", null);

    }

}