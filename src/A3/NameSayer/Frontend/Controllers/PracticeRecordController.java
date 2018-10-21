package A3.NameSayer.Frontend.Controllers;

import A3.NameSayer.Backend.Audio.Audio;
import A3.NameSayer.Backend.Audio.AudioRecordWorker;
import A3.NameSayer.Backend.Databases.Database;
import A3.NameSayer.Backend.Databases.UserDatabase;
import A3.NameSayer.Backend.Switch.SwitchScenes;
import A3.NameSayer.Backend.Switch.SwitchTo;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class PracticeRecordController implements Initializable {


    @FXML
    private Button recordButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button backButton;

    @FXML
    private Label label;

    private Database _database = Database.getInstance();
    private UserDatabase _userDatabase = UserDatabase.getInstance();
    private Process _currentProcess = AudioRecordWorker.pb;
    private Timeline _countProgress = new Timeline();



    public void buttonClick(ActionEvent e) throws IOException {
        if (e.getSource().equals(recordButton)) {

            if (recordButton.getText().equals("Stop")) {
                _countProgress.stop();
                _currentProcess = AudioRecordWorker.pb;

                if (_currentProcess != null) {
                    _currentProcess.destroy();
                    recordButton.setText("Record");
                }

                SwitchScenes.getInstance().switchScene(SwitchTo.PRACTICEQUERY, e, SwitchScenes.smallWidth, SwitchScenes.smallHeight);
            } else {
                recordButton.setText("Stop");
                backButton.setDisable(true);
                Audio audioUtil = new Audio();
                audioUtil.recordAudio(_userDatabase.getCurrentCustomName().startNewAttempt());
                startProgressBar(e);
            }


        }


        // Go back

        if (e.getSource().equals(backButton)) {
            Stage stage;
            stage=(Stage) backButton.getScene().getWindow();
            stage.close();
        }
    }

    // 5 Second progress bar


    private void startProgressBar(ActionEvent e) {
        EventHandler onFinished = (EventHandler<ActionEvent>) t -> {
            try {
                SwitchScenes.getInstance().switchScene(SwitchTo.PRACTICEQUERY, e, SwitchScenes.smallWidth, SwitchScenes.smallHeight);
            } catch (IOException err) {
                err.printStackTrace();
            }
        };

        _countProgress.setCycleCount(1);
        _countProgress.getKeyFrames().add(new KeyFrame(Duration.seconds(5), onFinished,new KeyValue(progressBar.progressProperty(), 1)));
        _countProgress.play();
        _countProgress.setOnFinished(event -> progressBar.setProgress(0.0));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText(_userDatabase.getCurrentCustomName().getName());
    }
}

