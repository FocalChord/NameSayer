package A3.NameSayer.Backend.Databases;

import A3.NameSayer.Backend.Items.Attempt;
import A3.NameSayer.Backend.Items.ColorItem;
import A3.NameSayer.Backend.Items.CustomName;
import A3.NameSayer.Backend.Items.DatabaseName;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class UserDatabase {

    public static final String PATH_TO_USERS_FOLDER = new File("Databases/test").getAbsolutePath();
    public static final String NO_ATTEMPTS = "No Attempts Yet!";

    private static UserDatabase _userDatabase;

    private ObservableList<ColorItem> _currentlySelectedColorItems = FXCollections.observableArrayList();
    private ObservableList<CustomName> _currentlySelectedNames = FXCollections.observableArrayList();

    private Map<String, CustomName> _allCustomNames = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private CustomName _selectedCustomName;
    private Attempt _selectedAttempt;



    // Initializing and reading from he database
    private UserDatabase() {

    }

    // Putting the singleton in "singleton"
    public static UserDatabase getInstance() {
        if (_userDatabase == null) {
            _userDatabase = new UserDatabase();
        }

        return _userDatabase;
    }

    public void setCurrentName(CustomName customName) {
        _selectedCustomName = customName;
    }

    public void setCurrentAttempt(Attempt attempt) {
        _selectedAttempt = attempt;
    }

    public List<DatabaseName> getListOfCurrentName() {
        return _selectedCustomName.getListOfNames();
    }

    public ObservableList<Attempt> getAttemptsOfCurrentName() {
        return _selectedCustomName.getListOfAttempts();
    }

    public String getNameOfCurrentName() {
        return _selectedCustomName.getName();
    }

    public String getCurrentRecordingPath() {
        return _selectedCustomName.startNewAttempt();
    }

    public CustomName getCurrentCustomName() {
        return _selectedCustomName;
    }

    public String getCurrentAttemptPath() {
        return _selectedAttempt.getAttemptPath();
    }

    public void updateCurrentlySelectedList(ObservableList<ColorItem> currentlySelectedColorItems) {
        _currentlySelectedColorItems = currentlySelectedColorItems;
    }

    public ObservableList<ColorItem> getCurrentlySelectedList() {
        return _currentlySelectedColorItems;
    }

    public void resetCurrentlySelectedList() {
        _currentlySelectedColorItems = FXCollections.observableArrayList();
        _currentlySelectedNames = FXCollections.observableArrayList();
    }

    public void makeCustomNames() {
        List<String> greenNames = _currentlySelectedColorItems.stream()
                .filter(e -> e.getColor().equals(Color.GREEN))
                .map(e -> e.getText())
                .collect(Collectors.toList());

        for (String name : greenNames) {
            CustomName tempCustomNameObj = _allCustomNames.get(name);

            if (tempCustomNameObj != null) {
                _currentlySelectedNames.add(tempCustomNameObj);
            } else {
                tempCustomNameObj = new CustomName(name);
                tempCustomNameObj.debug();
                _allCustomNames.put(name, tempCustomNameObj);
                _currentlySelectedNames.add(tempCustomNameObj);
            }
        }





    }

    public ObservableList<CustomName> getCurrentCustomNames() {
        return _currentlySelectedNames;
    }
}
