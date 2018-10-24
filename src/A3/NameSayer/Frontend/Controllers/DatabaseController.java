package A3.NameSayer.Frontend.Controllers;

import A3.NameSayer.Backend.Audio.Audio;
import A3.NameSayer.Backend.Audio.AudioPlayAttemptWorker;
import A3.NameSayer.Backend.Audio.AudioPlayMultipleNameWorker;
import A3.NameSayer.Backend.Databases.Database;
import A3.NameSayer.Backend.Databases.UserDatabase;
import A3.NameSayer.Backend.Items.Attempt;
import A3.NameSayer.Backend.Items.CustomName;
import A3.NameSayer.Backend.Items.DatabaseName;
import A3.NameSayer.Backend.Items.DatabaseNameProperties;
import A3.NameSayer.Backend.RatingSystem.Rating;
import A3.NameSayer.Backend.Switch.SwitchScenes;
import A3.NameSayer.Backend.Switch.SwitchTo;
import com.jfoenix.controls.JFXListView;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


public class DatabaseController implements Initializable {


    private static final String DELETE_ATTEMPT = new String("Are you sure you want to delete attempt?");
    private static final String DELETE_NAME = new String("Warning, this will delete all attempts");

    @FXML
    private TableView<DatabaseNameProperties> DatabaseTable;
    @FXML
    private JFXListView<CustomName> UserTable;
    @FXML
    private JFXListView<Attempt> UserAttemptsTable;
    @FXML
    private TableColumn<DatabaseNameProperties, String> nameColumn;
    @FXML
    private TableColumn<DatabaseNameProperties, String> ratingColumn;
    @FXML
    private Button listenButton1;
    @FXML
    private Button listenButton2;
    @FXML
    private Button deleteButton;
    @FXML
    private Button changeRatingButton;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab userTab;
    @FXML
    private Tab databaseTab;


    private Database _database = Database.getInstance();
    private UserDatabase _userDatabase = UserDatabase.getInstance();
    private Process _currentProcess = AudioPlayMultipleNameWorker.pb;
    private DatabaseNameProperties _currentDatabaseName;
    private boolean _databaseNamePlaying = false;
    private boolean _attemptPlaying = false;


    /**
     * Loads databaseTable and also the all the user recordings, places them all in the list and creates button bindings
     * for them
     * @param location
     * @param resources
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDatabaseTable();
        loadUserList();
        bindDatabaseButtons();
        initialiseTabPane();
    }

    /**
     * Loads the user list of all user recordings
     */

    private void loadUserList() {

        UserAttemptsTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> _userDatabase.setCurrentAttempt(newValue)));


        UserTable.itemsProperty().setValue(_userDatabase.getCustomNamesWithAttempts());

        UserTable.setCellFactory(lv -> new ListCell<CustomName>() {
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
        //Checks what name on the list is selected on the user recording list
        UserTable.getSelectionModel().selectedItemProperty().addListener((ob, oldVal, newVal) -> {
            if (newVal == null) {
                _userDatabase.setCurrentName(oldVal);
            } else {
                _userDatabase.setCurrentName(newVal);
            }

            UserAttemptsTable.itemsProperty().setValue(_userDatabase.getCurrentCustomName().getListOfAttempts());
            //Checks what name on the list is selected on the user attempt recording list
            UserAttemptsTable.setCellFactory(lv -> new ListCell<Attempt>() {
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
        });


    }


    private void loadDatabaseTable() {
        // Get the list of names in the database
        DatabaseTable.setItems(_database.getDatabaseTableList());

        // Set columns to the DatabaseName properties (name, rating)
        nameColumn.setCellValueFactory(data -> data.getValue().dbNameProperty());
        ratingColumn.setCellValueFactory(data -> data.getValue().dbRatingProperty());

        // Add listeners whenever an item is selected
        DatabaseTable.getSelectionModel().selectedItemProperty().addListener((ob, oldVal, newVal) -> {
            _database.setCurrentDatabaseName(newVal);
            _currentDatabaseName = newVal;
        });

        // Constrain size
        DatabaseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void bindDatabaseButtons() {
        // Get the observable list of current items
        ObservableList<DatabaseNameProperties> list = DatabaseTable.getSelectionModel().getSelectedItems();

        // If nothing is selected then disable buttons else enable
        changeRatingButton.disableProperty().bind(Bindings.isEmpty(list));
        listenButton1.disableProperty().bind(Bindings.isEmpty(list));

        listenButton2.disableProperty().bind(Bindings.and(
                Bindings.isEmpty(UserTable.getSelectionModel().getSelectedItems()),
                Bindings.isEmpty(UserAttemptsTable.getSelectionModel().getSelectedItems())
        ));

        deleteButton.disableProperty().bind(Bindings.and(
                Bindings.isEmpty(UserTable.getSelectionModel().getSelectedItems()),
                Bindings.isEmpty(UserAttemptsTable.getSelectionModel().getSelectedItems())
        ));
    }

    /**
     * Add binding to tabPane, so if there are no user recordings, the button is disabled
     */
    private void initialiseTabPane() {
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, userTab, databaseTab) -> {
                    if (!(DatabaseTable.getSelectionModel() == null)) {
                        DatabaseTable.getSelectionModel().clearSelection();

                    }

                }
        );

        userTab.disableProperty().bind(Bindings.isEmpty(UserTable.getItems()));

    }


    /**
     * Destroys audio so it stpos playing
     */
    private void closeProcess() {
        _currentProcess = AudioPlayMultipleNameWorker.pb;
        if (_currentProcess != null) {
            _currentProcess.destroyForcibly();
            listenButton2.setText("Listen");
        }
    }

    // When you click on the list view, deselect the UserTable attempts so database name can be played correctly

    public void handleClickUserTable() {
        UserAttemptsTable.getSelectionModel().clearSelection();
        UserAttemptsTable.refresh();
    }

    public void handleClickAttemptsTable() {
        //UserTable.getSelectionModel().clearSelection();
        //UserTable.refresh();
    }

    /**
     * When the user clicks on the listen button, get the current name and plays the audio for it
     */
    public void onListenDatabaseClick() {

        if (listenButton1.getText().equals("Stop Listening")) {
            closeProcess();
        } else {
            listenButton1.setText("Stop Listening");
            Audio audioUtil = new Audio();
            ArrayList<DatabaseName> databaseNames = new ArrayList<>();
            databaseNames.add(_database.getDatabaseObj(_currentDatabaseName.getDBName()));
            audioUtil.playAudio(databaseNames, listenButton1);
        }
    }

    /**
     * When user clicks on ChangeRating button, opens the rating screen
     */
    public void onChangeRatingClick() {
        showRatingAlert();
    }

    /**
     * When back button is pressed, stops the audio playback and switches to main menu
     * @param e
     * @throws IOException
     */
    public void onBackButtonClick(ActionEvent e) throws IOException {
        closeProcess();
        SwitchScenes.getInstance().switchScene(SwitchTo.MAINMENU, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
    }

    /**
     * Audio functionality for user recording attempts
     */
    public void onListenAttemptClick() {
        if (listenButton2.getText().equals("Stop")) {
            closeUserProcess();

        } else {
            Audio audioUtil = new Audio();
            if (UserAttemptsTable.getSelectionModel().isSelected(UserAttemptsTable.getSelectionModel().getSelectedIndex())) {
                _databaseNamePlaying = false;
                _attemptPlaying = true;
                listenButton2.setText("Stop");
                audioUtil.playAttempt(_userDatabase.getCurrentAttempt().getAttemptPath(), listenButton2);
            } else {
                _databaseNamePlaying = true;
                _attemptPlaying = false;
                listenButton2.setText("Stop");
                audioUtil.playAudio(_userDatabase.getCurrentCustomName().getListOfNames(), listenButton2);
            }
        }
    }

    /**
     * Shows the alerts when the user clicks the delete button
     */
    public void onDeleteClick() {
        if (UserAttemptsTable.getSelectionModel().isSelected(UserAttemptsTable.getSelectionModel().getSelectedIndex())) {
            showDeleteAlert(DELETE_ATTEMPT);
        } else {
            showDeleteAlert(DELETE_NAME);
        }

    }

    /**
     * Destroys audio process for attempts, and sets the button back to 'listen'
     */
    private void closeUserProcess() {
        if (_databaseNamePlaying) {
            _currentProcess = AudioPlayMultipleNameWorker.pb;
        } else {
            _currentProcess = AudioPlayAttemptWorker._pb;
        }

        if (_currentProcess != null) {
            _currentProcess.destroyForcibly();
            listenButton2.setText("Listen");
        }
    }

    /**
     * Alert that shows the rating screen
     */
    private void showRatingAlert() {
        ButtonType goodButton = new ButtonType("Good");
        ButtonType badButton = new ButtonType("Bad");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", goodButton, badButton);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getClassLoader().getResource("A3/NameSayer/Frontend/Styles/Alert.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(f -> alert.hide());
        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(res -> {
            //If the user presses good, then the rating for that name will be set to good, and the same for bad
            if (res.equals(goodButton)) {
                _database.changeRating(Rating.GOOD);
                DatabaseTable.refresh();
            } else if (res.equals(badButton)) {
                _database.changeRating(Rating.BAD);
                DatabaseTable.refresh();
            }
        });
    }

    /**
     * Alert that shows the delete screen
     * @param alertText
     */
    private void showDeleteAlert(String alertText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, alertText, ButtonType.YES, ButtonType.NO);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getClassLoader().getResource("A3/NameSayer/Frontend/Styles/Alert.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        alert.showAndWait();
        //If the user clicks yes, it will delete that attempt
        if (alert.getResult().equals(ButtonType.YES)) {
            if (alertText.equals(DELETE_ATTEMPT)) {
                _userDatabase.deleteAttempt();
                UserAttemptsTable.refresh();
            }
            //If they delete the full name and not attempt, it will give a different alert.
            if (alertText.equals(DELETE_NAME)) {
                _userDatabase.deleteName();
                UserTable.refresh();
            }

            if (UserTable.getItems().size() == 0 && UserAttemptsTable.getItems().size() == 0) {
                tabPane.getSelectionModel().select(databaseTab);
            }
        }
    }


}