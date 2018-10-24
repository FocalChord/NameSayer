package A3.NameSayer.Frontend.Controllers;

import A3.NameSayer.Backend.Audio.Audio;
import A3.NameSayer.Backend.Audio.AudioPlayAttemptWorker;
import A3.NameSayer.Backend.Audio.AudioPlayMultipleNameWorker;
import A3.NameSayer.Backend.Databases.UserDatabase;
import A3.NameSayer.Backend.Items.Attempt;
import A3.NameSayer.Backend.Items.CustomName;
import A3.NameSayer.Backend.Items.DatabaseName;
import A3.NameSayer.Backend.Switch.SwitchScenes;
import A3.NameSayer.Backend.Switch.SwitchStage;
import A3.NameSayer.Backend.Switch.SwitchTo;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class PracticeModeController implements Initializable {



    @FXML
    private Button listenButton;

    @FXML
    private Button recordButton;


    @FXML
    private ListView<CustomName> namePracticeList;

    @FXML
    private ListView<Attempt> attemptsList;



    private UserDatabase _userDatabase = UserDatabase.getInstance();
    private Process _currentProcess = AudioPlayMultipleNameWorker.pb;

    private boolean _databaseNamePlaying = false;
    private boolean _attemptPlaying = false;

    /**
     * Initialise all bindings and get the list of seletced names from the previous scene
     * @param location
     * @param resources
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get the observable list of current items
        ObservableList<CustomName> list = namePracticeList.getSelectionModel().getSelectedItems();
        ObservableList<Attempt> list1 = attemptsList.getSelectionModel().getSelectedItems();

        // If nothing is selected then disable buttons else enable

        listenButton.disableProperty().bind(Bindings.and(
                Bindings.isEmpty(list),
                Bindings.isEmpty(list1)
        ));

        recordButton.disableProperty().bind(Bindings.and(
                Bindings.isEmpty(list),
                Bindings.isEmpty(list1)
        ));

        setUpLists();
    }


    /**
     * Load both lists (first list is for names and the second is attempts related to them)
     */

    private void setUpLists() {
        namePracticeList.itemsProperty().setValue(_userDatabase.getCurrentCustomNames());

        namePracticeList.setCellFactory(lv -> new ListCell<CustomName>() {
            @Override
            public void updateItem(CustomName item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        attemptsList.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> _userDatabase.setCurrentAttempt(newValue)));

        namePracticeList.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                _userDatabase.setCurrentName(oldValue);
            } else {
                _userDatabase.setCurrentName(newValue);
            }
            attemptsList.itemsProperty().setValue(_userDatabase.getCurrentCustomName().getListOfAttempts());
            attemptsList.setCellFactory(lv -> new ListCell<Attempt>() {
                @Override
                public void updateItem(Attempt item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getAttemptName());
                    }
                }
            });
        }));

    }

    /**
     * If the user clicks on the other list, clear selection of the previous list
     */
    public void handleClickListView() {
        attemptsList.getSelectionModel().clearSelection();
        attemptsList.refresh();
    }

    /**
     * When the record button is pressed, open the new stage
     * @throws IOException
     */
    public void onRecordClick() throws IOException {
        SwitchStage.getInstance().switchStage(SwitchTo.RECORDSTAGE);
    }

    /**
     * When the random button is pressed, shuffle the list
     */

    public void onRandomClick() {
        Collections.shuffle(namePracticeList.getItems());
    }

    /**
     * When the main menu button is pressed, clear all the current selections of names, and take the user to the
     * main menu
     * @param e
     * @throws IOException
     */

    public void onMainMenuClick(ActionEvent e) throws IOException {
        _userDatabase.closeSession();
        _userDatabase.clearCustomNames();
        _userDatabase.resetCurrentlySelectedList();
        closeProcess();
        SwitchScenes.getInstance().switchScene(SwitchTo.MAINMENU, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
    }

    /**
     * Take the user back to the PracticeChoose scene
     * @param e
     * @throws IOException
     */
    public void onBackClick(ActionEvent e) throws IOException {
        _userDatabase.clearCustomNames();
        closeProcess();
        SwitchScenes.getInstance().switchScene(SwitchTo.PRACTICECHOOSE, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
    }

    /**
     * When the user clicks on the listen button, carries out the various audio processing, and the user will hear
     * the current name selected
     */
    public void onListenClick() {
        if (listenButton.getText().equals("Stop")) {
            closeProcess();
        } else {
            Audio audioUtil = new Audio();
            if (attemptsList.getSelectionModel().isSelected(attemptsList.getSelectionModel().getSelectedIndex())) {
                //If there is no attempt for that current name, give an error
                if (attemptsList.getItems().get(0).getAttemptName().equals(UserDatabase.NO_ATTEMPTS)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Make an attempt!");
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(getClass().getClassLoader().getResource("A3/NameSayer/Frontend/Styles/Alert.css").toExternalForm());
                    dialogPane.getStyleClass().add("myDialog");
                    alert.showAndWait();
                } else {
                    //Play the audio and set the listen button to a stpo button
                    _databaseNamePlaying = false;
                    _attemptPlaying = true;
                    listenButton.setText("Stop");
                    audioUtil.playAttempt(_userDatabase.getCurrentAttempt().getAttemptPath(), listenButton);
                }
            } else {
                //Play the database name if the user recording attempt is not selected
                _databaseNamePlaying = true;
                _attemptPlaying = false;
                listenButton.setText("Stop");
                List<DatabaseName> databaseNames = _userDatabase.getCurrentCustomName().getListOfNames();
                audioUtil.playAudio(databaseNames, listenButton);
            }
        }

    }

    /**
     * Destroys the process so that the audio stops playing
     */
    private void closeProcess() {
        if (_databaseNamePlaying) {
            _currentProcess = AudioPlayMultipleNameWorker.pb;
        } else {
            _currentProcess = AudioPlayAttemptWorker._pb;
        }

        if (_currentProcess != null) {
            _currentProcess.destroyForcibly();
            listenButton.setText("Listen");
        }
    }
}

