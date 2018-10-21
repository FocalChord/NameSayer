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
    private static final String DELETE_NAME = new String("Are you sure you want to delete this name and all attempts?");

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
    private Button backButton1;
    @FXML
    private Button backButton2;
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


    // Load database and table

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDatabaseTable();
        loadUserList();
        bindDatabaseButtons();
        initialiseTabPane();
    }

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

        UserTable.getSelectionModel().selectedItemProperty().addListener((ob, oldVal, newVal) -> {
            if (newVal == null) {
                _userDatabase.setCurrentName(oldVal);
            } else {
                _userDatabase.setCurrentName(newVal);
            }

            UserAttemptsTable.itemsProperty().setValue(_userDatabase.getCurrentCustomName().getListOfAttempts());

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
    }

    private void initialiseTabPane() {
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, userTab, databaseTab) -> {
                    if (!(DatabaseTable.getSelectionModel() == null)) {
                        DatabaseTable.getSelectionModel().clearSelection();

                    }
                    //deleteButton.setDisable(true);
                    //listenButton2.setDisable(true);
                    //UserTable.getSelectionModel().clearSelection();
                    //UserAttemptsTable.getSelectionModel().clearSelection();
                }
        );
    }


    private void loadTable() {

    }


    private void closeProcess() {
        _currentProcess = AudioPlayMultipleNameWorker.pb;
        if (_currentProcess != null) {
            _currentProcess.destroyForcibly();
            listenButton2.setText("Listen");
        }
    }

    // When you click on the list view, deselect the UserTable attempts so database name can be played correctly

    public void handleClickListView() {

    }

    public void handleClickUserTable() {
        UserAttemptsTable.getSelectionModel().clearSelection();
        UserAttemptsTable.refresh();
    }

    public void handleClickAttemptsTable() {
        //UserTable.getSelectionModel().clearSelection();
        //UserTable.refresh();
    }

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

    public void onChangeRatingClick() {
        showRatingAlert();
    }

    public void onBackButtonClick(ActionEvent e) throws IOException {
        closeProcess();
        SwitchScenes.getInstance().switchScene(SwitchTo.MAINMENU, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
    }

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

    public void onDeleteClick() {
        if (UserAttemptsTable.getSelectionModel().isSelected(UserAttemptsTable.getSelectionModel().getSelectedIndex())) {
            showDeleteAlert(DELETE_ATTEMPT);
            // Delete Attempt
        } else {
            showDeleteAlert(DELETE_NAME);
            // Delete User Attempts
        }

    }

    private void closeUserProcess() {
        if (_databaseNamePlaying) {
            _currentProcess = AudioPlayMultipleNameWorker.pb;
        } else {
            _currentProcess = AudioPlayAttemptWorker.pb;
        }

        if (_currentProcess != null) {
            _currentProcess.destroyForcibly();
            listenButton2.setText("Listen");
        }
    }

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
            if (res.equals(goodButton)) {
                _database.changeRating(Rating.GOOD);
                DatabaseTable.refresh();
            } else if (res.equals(badButton)) {
                _database.changeRating(Rating.BAD);
                DatabaseTable.refresh();
            }
        });
    }

    private void showDeleteAlert(String alertText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, alertText, ButtonType.YES, ButtonType.NO);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getClassLoader().getResource("A3/NameSayer/Frontend/Styles/Alert.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        alert.showAndWait();
        if (alert.getResult().equals(ButtonType.YES)) {
            if (alertText.equals(DELETE_ATTEMPT)) {

            }

            if (alertText.equals(DELETE_NAME)) {
                _userDatabase.deleteName();
            }
        }
    }


}