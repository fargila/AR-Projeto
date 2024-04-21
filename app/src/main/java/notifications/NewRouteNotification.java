package notifications;

import utils.NewRouteForm;
import states.MainMenuState;
import resources.ResourceManager;
import android.content.DialogInterface;
import main.ProjectAR;

public class NewRouteNotification implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        MainMenuState state = (MainMenuState) (ProjectAR.getInstance()
                .getCurrentState());
        NewRouteForm form = state.getBlankForm();
        ResourceManager.getInstance().createXMLTemplate(form.getFileName(),
                form.getRouteName(), -2, form.getNumPoints());
        dialog.dismiss();
    }

}
