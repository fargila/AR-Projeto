package notifications;

import main.ProjectAR;
import states.MainMenuState;
import utils.NewRouteForm;
import android.content.DialogInterface;

public class UserOptionsTemplateNotification implements
        DialogInterface.OnClickListener {

    private final MainMenuState state = (MainMenuState) (ProjectAR.getInstance()
            .getCurrentState());
    private final CharSequence[] options = new CharSequence[state
            .getTempFiles().length + 1];

    @Override
    public void onClick(DialogInterface arg0, int arg1) {
        switch (arg1) {
            case 0:
                NewRouteForm form = new NewRouteForm(ProjectAR.getInstance());
                state.setBlankForm(form);
                ProjectAR.getInstance().createCustomDialog(
                        "Nueva ruta: plantilla en blanco", form,
                        new NewRouteNotification(), "Aceptar", null);
                break;
            case 1:
                options[0] = "Nueva ruta";
                int i = 1;
                for (String s : state.getTempFiles()) {
                    options[i] = "Continuar con: " + s;
                    i++;
                }
                ProjectAR.getInstance().createDialog(
                        "Nueva ruta: mediante captura de puntos", options,
                        new GeoTaggingButton(), null);
                break;

        }

        arg0.dismiss();
    }

}
