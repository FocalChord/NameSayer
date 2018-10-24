package A3.NameSayer.Frontend.Controllers;

import A3.NameSayer.Backend.Audio.Audio;
import A3.NameSayer.Backend.Audio.AudioCompareWorker;
import A3.NameSayer.Backend.Databases.UserDatabase;
import A3.NameSayer.Backend.Items.CustomName;
import A3.NameSayer.Backend.Items.DatabaseName;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class PracticeQueryController implements Initializable {
    @FXML
    public Button compareButton;


    @FXML
    private Button keepButton;


    @FXML
    private Label label;


    private UserDatabase _userDatabase = UserDatabase.getInstance();
    private Process _currentProcess = AudioCompareWorker.pb;


    /**
     * When this stage is open, set the title to the name the user is practicing
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText(_userDatabase.getCurrentCustomName().getName());
    }

    /**
     * Saves the recording and closes the stage
     */
    public void onKeepClick() {
        _userDatabase.getCurrentCustomName().keepCurrentAttempt();
        closeStage();

    }

    /**
     * Plays the database audio, and then the user recording. When user clicks compare, changes the button to a
     * stop button
     */

    public void onCompareClick() {
        if (compareButton.getText().equals("Stop Comparing")) {
            _currentProcess = AudioCompareWorker.pb;
            closeProcess();
        } else {
            compareButton.setText("Stop Comparing");
            Audio audioUtil = new Audio();
            CustomName currentName = _userDatabase.getCurrentCustomName();
            audioUtil.compareRecordings(currentName.getListOfNames(), currentName.startNewAttempt(), compareButton);
        }

    }

    /**
     * When the user clicks on the exit button, close the stage
     */

    public void onExitClick() {
        _userDatabase.getCurrentCustomName().deleteCurrentAttempt();
        closeStage();

    }

    /**
     * Gets the current stage and closes it
     */
    private void closeStage() {
        Stage stage = (Stage) keepButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Destroys the process so the audio stops playing
     */
    private void closeProcess() {
        if (_currentProcess != null) {
            _currentProcess.destroyForcibly();
            compareButton.setText("Compare");
        }
    }
}
