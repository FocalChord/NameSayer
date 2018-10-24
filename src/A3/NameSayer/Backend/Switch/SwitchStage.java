package A3.NameSayer.Backend.Switch;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class SwitchStage {

    private static SwitchStage _switchStage;

    // Singleton constructor

    private SwitchStage() {}
    public static SwitchStage getInstance() {
        if (_switchStage == null) {
            _switchStage = new SwitchStage();
        }

        return _switchStage;
    }

    /**
     * This switches scene to the required stage
     * @param sceneE this is a enum the user is required to input
     * @throws IOException
     */
    public void switchStage(SwitchTo sceneE) throws IOException {
        FXMLLoader loader = sceneE.getLoader();
        Parent sParent = loader.load();
        Scene sScene = new Scene(sParent, 700, 340);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(sScene);
        stage.show();
    }



}
