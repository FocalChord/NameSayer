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
    private Button backButton;

    @FXML
    private Button listenButton;

    @FXML
    private Button recordButton;

    @FXML
    private Button randomiseButton;

    @FXML
    private Button mainMenuButton;

    @FXML
    private ListView<CustomName> namePracticeList;

    @FXML
    private ListView<Attempt> attemptsList;



    private UserDatabase _userDatabase = UserDatabase.getInstance();
    private Process _currentProcess = AudioPlayMultipleNameWorker.pb;

    private boolean databaseNamePlaying = false;
    private boolean attemptPlaying = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get the observable list of current items
        ObservableList<CustomName> list = namePracticeList.getSelectionModel().getSelectedItems();
        ObservableList<Attempt> list1 = attemptsList.getSelectionModel().getSelectedItems();

        // If nothing is selected then disable buttons else enable
        //listenButton.disableProperty().bind(Bindings.isEmpty(list));

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


    // Load both lists (first list is for names and the second is attempts related to them)

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

    public void handleClickListView() {
        attemptsList.getSelectionModel().clearSelection();
        attemptsList.refresh();
    }

    public void onRecordClick() throws IOException {
        SwitchStage.getInstance().switchStage(SwitchTo.RECORDSTAGE);
    }

    public void onRandomClick() {
        Collections.shuffle(namePracticeList.getItems());
    }

    public void onMainMenuClick(ActionEvent e) throws IOException {
        _userDatabase.closeSession();
        _userDatabase.clearCustomNames();
        _userDatabase.resetCurrentlySelectedList();
        closeProcess();
        SwitchScenes.getInstance().switchScene(SwitchTo.MAINMENU, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
    }

    public void onBackClick(ActionEvent e) throws IOException {
        _userDatabase.clearCustomNames();
        closeProcess();
        SwitchScenes.getInstance().switchScene(SwitchTo.PRACTICECHOOSE, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
    }

    public void onListenClick() {
        if (listenButton.getText().equals("Stop")) {
            System.out.println("database playing " + databaseNamePlaying);
            System.out.println(" attempt playing " + attemptPlaying);
            closeProcess();
        } else {
            Audio audioUtil = new Audio();
            if (attemptsList.getSelectionModel().isSelected(attemptsList.getSelectionModel().getSelectedIndex())) {
                if (attemptsList.getItems().get(0).getAttemptName().equals(UserDatabase.NO_ATTEMPTS)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Make an attempt!");
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(getClass().getClassLoader().getResource("A3/NameSayer/Frontend/Styles/Alert.css").toExternalForm());
                    dialogPane.getStyleClass().add("myDialog");
                    alert.showAndWait();
                } else {
                    databaseNamePlaying = false;
                    attemptPlaying = true;
                    listenButton.setText("Stop");
                    audioUtil.playAttempt(_userDatabase.getCurrentAttempt().getAttemptPath(), listenButton);
                }
            } else {
                databaseNamePlaying = true;
                attemptPlaying = false;
                listenButton.setText("Stop");
                List<DatabaseName> databaseNames = _userDatabase.getCurrentCustomName().getListOfNames();
                audioUtil.playAudio(databaseNames, listenButton);
            }
        }

    }

    private void closeProcess() {
        if (databaseNamePlaying) {
            _currentProcess = AudioPlayMultipleNameWorker.pb;
        } else {
            _currentProcess = AudioPlayAttemptWorker.pb;
        }

        if (_currentProcess != null) {
            _currentProcess.destroyForcibly();
            listenButton.setText("Listen");
        }
    }
}

