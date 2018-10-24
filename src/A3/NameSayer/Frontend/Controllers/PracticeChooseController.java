package A3.NameSayer.Frontend.Controllers;


import A3.NameSayer.Backend.Items.ColorItem;
import A3.NameSayer.Backend.Databases.Database;
import A3.NameSayer.Backend.Databases.UserDatabase;
import A3.NameSayer.Backend.Switch.SwitchScenes;
import A3.NameSayer.Backend.Switch.SwitchTo;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class PracticeChooseController implements Initializable {

    @FXML
    private Button uploadButton;

    @FXML
    private Button backButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ListView<ColorItem> listViewSelect;

    @FXML
    private ListView<String> listViewDatabase;

    @FXML
    private TextField filterInput;

    private Database _database = Database.getInstance();
    private UserDatabase _userDatabase = UserDatabase.getInstance();
    private int _index = 0;
    private int _counter = 0;

    /**
     * Initialise the scene by loading the listeners, cell factories and binding the observable lists to the buttons
     * @param location
     * @param resources
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadListViewDatabase();
        loadListViewSelect();
        bindButtons();
    }

    /**
     * Bind the buttons to selection of names
     */

    private void bindButtons() {
        // Get the observable list of current items
        ObservableList<ColorItem> list = listViewSelect.getItems();

        // If nothing is selected then disable buttons else enable
        nextButton.disableProperty().bind(Bindings.isEmpty(list));
        deleteButton.disableProperty().bind(Bindings.isEmpty(list));
    }

    /**
     * Initialises the list selection view, and refreshes it so the list is constantly updated
     */

    private void loadListViewSelect() {


        listViewSelect.itemsProperty().setValue(_userDatabase.getCurrentlySelectedList());

        listViewSelect.getItems().addListener((ListChangeListener<ColorItem>) c -> {
            _userDatabase.updateCurrentlySelectedList(listViewSelect.getItems());
        });

        //If the name is in the database colour it green, otherwise colour it red
        listViewSelect.setCellFactory(lv -> new ListCell<ColorItem>() {
            @Override
            protected void updateItem(ColorItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setText(null);
                    setTextFill(null);
                } else if (item.inDatabase()) {
                    setText(item.getText());
                    setTextFill(Color.GREEN);
                } else if (!item.inDatabase()) {
                    setText(item.getText());
                    setTextFill(Color.RED);
                }
            }
        });

        listViewSelect.refresh();
    }

    /**
     * Double click to add names and loads the list view
     */

    private void loadListViewDatabase() {
        listViewDatabase.setFocusTraversable(false);

        // When you double click on selected item on listView
        listViewDatabase.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String name = listViewDatabase.getSelectionModel().getSelectedItem();
                filterInput.appendText(name + " ");
            }
        });
        //Binds list view to input
        addListenerToTextField(_database.getDatabaseNameList());
    }

    //Gives the ability to delete names from the selection list
    public void onDeleteClick() {

        if (listViewSelect.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        ColorItem item = listViewSelect.getSelectionModel().getSelectedItem();

        int index = 0;

        for (ColorItem c : listViewSelect.getItems()) {
            if (c.getText().equals(item.getText())) {
                listViewSelect.getItems().remove(index);
                break;
            }

            index++;
        }

    }

    /**
     * Allows for the uploading of TXT files that contain names. When the TXT file is uploaded, it displays the list
     * of names on the selected list.
     * @throws IOException
     */
    public void onUploadClick() throws IOException {
        File file = getFile();

        if (file != null) {
            String path = file.getAbsolutePath();
            BufferedReader reader = new BufferedReader(new FileReader(new File(path)));

            String line = null;

            //Get each line of the text file
            while ((line = reader.readLine()) != null) {
                Boolean added = false;

                //If a line contains concatenated names with a space or dash, put each name into a string array
                String[] splitline = line.trim().split("[\\s-]+");

                int length = splitline.length;
                _counter = 0;

                //Go through the list of names in the database
                for (int i = 0; i < _database.getDatabaseNameList().size(); i++) {
                    //Go through each name in the array
                    for (int j = 0; j < length; j++) {
                        //If the name is in the database then add it to the selected list
                        if (splitline[j].toLowerCase().equals(_database.getDatabaseNameList().get(i).toString().toLowerCase())) {
                            _counter++;
                            //This is to ensure that each separate name in the concatenated name are all in the database
                            if (_counter == length) {
                                String line2 = capitalise(line);
                                //If the same name is repeated twice, only add it once
                                ColorItem item = new ColorItem(line2, Color.GREEN, true);
                                if (listViewSelect.getItems().stream().anyMatch(x -> x.getText().equals(line2))){
                                    added = true;
                                } else {
                                    listViewSelect.getItems().add(item);
                                    added = true;
                                }
                            }
                        }
                    }
                }
                //If the name is not in the database, colour it red
                if (!added) {
                    ColorItem item = new ColorItem(line, Color.RED, false);
                    listViewSelect.getItems().add(item);
                }
            }
        }
    }

    /**
     * When next is pressed, the green coloured names are carried over, and switch the scene to PracticeMode
     * @param e
     * @throws IOException
     */
    public void onNextClick(ActionEvent e) throws IOException {
        _userDatabase.makeCustomNames();
        SwitchScenes.getInstance().switchScene(SwitchTo.PRACTICEMODE, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
    }

    /**
     * When the user clicks Back, clears the name selection and goes to MainMenu
     * @param e
     * @throws IOException
     */
    public void onBackClick(ActionEvent e) throws IOException {
        _userDatabase.closeSession();
        _userDatabase.clearCustomNames();
        _userDatabase.resetCurrentlySelectedList();
        SwitchScenes.getInstance().switchScene(SwitchTo.MAINMENU, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
    }


    public void onAddClick() {
        parseTextField();
    }

    /**
     * Filters the input list according to the letters typed in the TextField, i.e If a user types a, only names that start
     * with a will be shown in the input list.
     * @param filteredData
     */

    private void addListenerToTextField(FilteredList<String> filteredData) {
        filterInput.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                parseTextField();
            }
        });

        filterInput.textProperty().addListener((obs, oldValue, newValue) -> {
            String filter = filterInput.getText();
            if (filter.length() == 0) {
                filteredData.setPredicate(s -> true);
            } else {
                if (filter.charAt(filter.length() - 1) == ' ' || filter.charAt(filter.length() - 1) == '-') {
                    _index = filter.length();
                    filteredData.setPredicate(s -> true);
                } else {
                    if (_index > filter.length()) {
                        _index = filter.lastIndexOf(' ') + 1;
                        if (_index == -1) {
                            _index = 0;
                        }
                    }
                    filteredData.setPredicate(s -> {
                        String text = filter.substring(_index, filter.length()).toLowerCase();
                        if (s.length() >= text.length()) {
                            if (s.toLowerCase().substring(0, text.length()).equals(text)) {
                                return true;
                            }
                        }
                        return newValue == null || newValue.isEmpty();
                    });
                }
            }
            listViewDatabase.refresh();
        });
        listViewDatabase.itemsProperty().setValue(filteredData);
    }


    /**
     * Takes whatever is typed into the TextField and parses it so that every first letter of each name is the correct
     * format
     */
    private void parseTextField() {
        String input = filterInput.getText();
        input = input.trim();
        String[] splitline = input.split("[\\s-]+");
        int length = splitline.length;

        int counter = 0;
        boolean added = false;
        //Checks if each separate name in the concatenated name is in the database
        for (int i = 0; i < _database.getDatabaseNameList().size(); i++) {
            for (int j = 0; j < length; j++) {
                if (splitline[j].toLowerCase().equals(_database.getDatabaseNameList().get(i).toLowerCase())) {
                    counter++;
                    if (counter == length) {
                        String output = capitalise(input);
                        ColorItem item = new ColorItem(output, Color.GREEN, true);
                        //If the name is already on the selected list, then give an alert
                        if (listViewSelect.getItems().stream().anyMatch(x -> x.getText().equals(output))) {
                            added = true;
                            showAlert("Name already in list");
                            //Add it to the list if every name is in the database
                        } else {
                            listViewSelect.getItems().add(item);
                            added = true;
                            filterInput.clear();
                        }
                    }
                }
            }
        }

        if (!added) {
            showAlert("Name does not exist in database");
        }

    }

    /**
     * Takes a name and makes the first letter capitalised, with all other letters lowercase. Spaces are also removed
     * @param name
     * @return the name capitalised with spaces removed
     */

    private String capitalise(String name) {
        name = name.trim().replaceAll("\\s+", " ").toLowerCase();

        String result = "";
        name = name.replace("() ([A-Z]}", "$1 $2");
        String[] words = name.split("[\\s-]+");
        for (String word : words) {
            result += Character.toUpperCase(word.charAt(0)) + word.substring(1) + " ";
        }

        result = result.trim();
        return result;
    }

    /**
     * Contains functionality for alert messages and also styles the alert messages with css
     * @param alertText
     */
    private void showAlert(String alertText) {
        Alert alert = new Alert(Alert.AlertType.ERROR, alertText);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getClassLoader().getResource("A3/NameSayer/Frontend/Styles/Alert.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        alert.showAndWait();
        filterInput.clear();
    }

    /**
     * Contains the functionality that opens the FileChooser when the user clicks upload
     * @return
     */
    private File getFile() {
        String userDir = System.getProperty("user.dir");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(userDir));

        //Filter so that only TXT files are allowed to be seletced
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);

        return file;

    }


}
