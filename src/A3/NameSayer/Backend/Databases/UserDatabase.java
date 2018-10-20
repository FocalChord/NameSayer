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
    private ObservableList<CustomName> _allCustomNamesList = FXCollections.observableArrayList();

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



    public CustomName getCurrentCustomName() {
        return _selectedCustomName;
    }

    public Attempt getCurrentAttempt() { return _selectedAttempt; }

    public ObservableList<ColorItem> getCurrentlySelectedList() {
        return _currentlySelectedColorItems;
    }

    public ObservableList<CustomName> getCurrentCustomNames() {
        return _currentlySelectedNames;
    }

    public ObservableList<CustomName> getCustomNamesWithAttempts() {
        return _allCustomNamesList;
    }

    public void closeSession() {
        Map<String, CustomName> tempMap = _allCustomNames.entrySet().stream()
                .filter(e -> !e.getValue().noAttempts())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        _allCustomNames = tempMap;

        ArrayList<CustomName> allVals = new ArrayList<>(_allCustomNames.values());
        _allCustomNamesList = FXCollections.observableArrayList(allVals);
    }

    public void setCurrentName(CustomName customName) {
        _selectedCustomName = customName;
    }

    public void setCurrentAttempt(Attempt attempt) {
        _selectedAttempt = attempt;
    }

    public void updateCurrentlySelectedList(ObservableList<ColorItem> currentlySelectedColorItems) {
        _currentlySelectedColorItems = currentlySelectedColorItems;
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
                _allCustomNames.put(name, tempCustomNameObj);
                _currentlySelectedNames.add(tempCustomNameObj);
            }
        }

        System.out.println(_allCustomNames);
    }

    public void clearCustomNames() {
        _currentlySelectedNames.clear();

    }
}
