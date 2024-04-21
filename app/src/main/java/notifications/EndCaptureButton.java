package notifications;

import main.ProjectAR;
import android.content.DialogInterface;

public class EndCaptureButton implements DialogInterface.OnClickListener {

    private final CharSequence[] options = { "Guardar temporariamente",
            "Exportar para XML" };

    @Override
    public void onClick(DialogInterface dialog, int which) {
        ProjectAR.getInstance().createDialog("Finalizar rota", options,
                new ExportPointsButton(), null);
    }

}

