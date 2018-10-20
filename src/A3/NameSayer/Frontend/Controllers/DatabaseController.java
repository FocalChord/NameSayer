package A3.NameSayer.Frontend.Controllers;

import A3.NameSayer.Backend.Audio.Audio;
import A3.NameSayer.Backend.Items.DatabaseName;
import A3.NameSayer.Backend.Items.DatabaseNameProperties;
import A3.NameSayer.Backend.Audio.AudioPlayMultipleNameWorker;
import A3.NameSayer.Backend.RatingSystem.Rating;
import A3.NameSayer.Backend.Switch.SwitchScenes;
import A3.NameSayer.Backend.Switch.SwitchTo;
import A3.NameSayer.Backend.Databases.Database;
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


    @FXML
    private TableView<DatabaseNameProperties> DatabaseTable;

    @FXML
    private JFXListView<String> UserTable;

    @FXML
    private JFXListView<String> UserAttemptsTable;


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
    private Process _currentProcess = AudioPlayMultipleNameWorker.pb;
    private DatabaseNameProperties _currentDatabaseName;


    // Load database and table

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDatabaseTable();
        loadUserTable();
        bindDatabaseButtons();
        initialiseTabPane();
    }

    private void loadUserTable() {
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
    }

    private void initialiseTabPane() {
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, userTab, databaseTab) -> {
                    if (!(DatabaseTable.getSelectionModel() == null)) {
                        DatabaseTable.getSelectionModel().clearSelection();

                    }
                    deleteButton.setDisable(true);
                    listenButton2.setDisable(true);
                    UserTable.getSelectionModel().clearSelection();
                    UserAttemptsTable.getSelectionModel().clearSelection();
                }
        );
    }


    private void loadTable() {

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
        listenButton2.setDisable(false);
        deleteButton.setDisable(false);
        loadTable();
    }

    public void handleClickAttemptsTable() {
        UserTable.getSelectionModel().clearSelection();
        UserTable.refresh();
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
    }

    public void onDeleteClick() {
    }
}