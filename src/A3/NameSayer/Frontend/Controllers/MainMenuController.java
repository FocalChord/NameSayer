package A3.NameSayer.Frontend.Controllers;

import A3.NameSayer.Backend.Switch.SwitchStage;
import A3.NameSayer.Backend.Switch.SwitchTo;
import A3.NameSayer.Backend.Switch.SwitchScenes;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class  MainMenuController implements Initializable {

    @FXML
    private Button practiceButton;

    @FXML
    private Button databaseButton;

    @FXML
    private Button soundCheckButton;

    @FXML
    private Button statisticsButton;
    

    public void buttonClick(ActionEvent e) throws IOException {

        // Switch Scene to go to required scene

        if (e.getSource().equals(practiceButton)) {
            SwitchScenes.getInstance().switchScene(SwitchTo.PRACTICECHOOSE, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
        }

        if (e.getSource().equals(databaseButton)) {
            SwitchScenes.getInstance().switchScene(SwitchTo.DB, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
        }

        if (e.getSource().equals(soundCheckButton)) {
            SwitchScenes.getInstance().switchScene(SwitchTo.SOUNDCHECK, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);

        }
        if (e.getSource().equals(statisticsButton)) {
            SwitchScenes.getInstance().switchScene(SwitchTo.STATISTICS, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Stage pStage = SwitchStage.getInstance().getPrivateStage();
        pStage.setOnHiding(event -> Platform.runLater(() -> {

        }));

    }
}

