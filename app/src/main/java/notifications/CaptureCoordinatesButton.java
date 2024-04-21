package notifications;

import java.util.ArrayList;

import android.content.DialogInterface;
import main.ProjectAR;
import main.PointOI;
import states.MainMenuState;
import utils.PointExtraInfoGeoTagging;

public class CaptureCoordinatesButton implements
        DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface arg0, int arg1) {
        MainMenuState state = (MainMenuState) ProjectAR.getInstance()
                .getCurrentState();
        ArrayList<PointOI> points = state.getGeoTaggingPoints();
        PointOI point = new PointOI();
        point.id = points.size();
        point.coords[0] = state.getCoordinatesCapturer().getLatitude();
        point.coords[1] = state.getCoordinatesCapturer().getLontitude();
        state.setCurrentPoint(point);

        PointExtraInfoGeoTagging info = new PointExtraInfoGeoTagging(ProjectAR
                .getInstance().getApplicationContext());
        state.setExtraInfo(info);
        ProjectAR.getInstance().createCustomDialog(
                "Información extra del nuevo punto", info,
                new ExtraInfoPointButton(), "Guardar datos",
                new NoExtraInfoButton(), "Saltar paso", null);

        arg0.dismiss();
    }

}
