package A3.NameSayer.Backend.Databases;

import A3.NameSayer.Backend.Items.Attempt;
import A3.NameSayer.Backend.Items.ColorItem;
import A3.NameSayer.Backend.Items.CustomName;
import A3.NameSayer.Backend.Items.CustomNameSerializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
    private UserDatabase() { }

    // Putting the singleton in "singleton"
    public static UserDatabase getInstance() {
        if (_userDatabase == null) {
            _userDatabase = new UserDatabase();
        }

        return _userDatabase;
    }


    /**
     * This function gets the currently selected name either in database controller or practice mode controller
     * @return A custom name object which is currently selected in the controller
     */

    public CustomName getCurrentCustomName() {
        return _selectedCustomName;
    }

    /**
     * This function gets the currently selected attempt either in database controller or practice mode controller
     * @return An attempt object which is currently selected in either controller
     */

    public Attempt getCurrentAttempt() {
        return _selectedAttempt;
    }

    /**
     * This function sets the current attempt being selected on the list view in database controller and practice mode
     * controller
     * @param attempt
     */

    public void setCurrentAttempt(Attempt attempt) {
        _selectedAttempt = attempt;
    }

    /**
     * This function returns an observable list of the currently selected coloritems in practice choose controller
     * @return an observable list of color items
     */

    public ObservableList<ColorItem> getCurrentlySelectedList() {
        return _currentlySelectedColorItems;
    }

    /**
     * This function returns an observable list of the currently selected custom names in practice mode controller
     * @return an observable list of custom names
     */

    public ObservableList<CustomName> getCurrentCustomNames() {
        return _currentlySelectedNames;
    }

    /**
     * This function returns an observable list of the custom names with attempts
     * @return an observable list of custom names
     */

    public ObservableList<CustomName> getCustomNamesWithAttempts() {
        return _allCustomNamesList;
    }

    /**
     * This function closes any session by filtering out all the custom names with no attempts
     */

    public void closeSession() {
        Map<String, CustomName> tempMap = _allCustomNames.entrySet().stream()
                .filter(e -> !e.getValue().noAttempts())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        _allCustomNames = tempMap;

        ArrayList<CustomName> allVals = new ArrayList<>(_allCustomNames.values());
        _allCustomNamesList = FXCollections.observableArrayList(allVals);
    }

    /**
     * This function sets the current name to the selected custom name
     * @param customName
     */

    public void setCurrentName(CustomName customName) {
        _selectedCustomName = customName;
    }

    /**
     * This function constantly refreshes the currently selected color items
     * @param currentlySelectedColorItems
     */

    public void updateCurrentlySelectedList(ObservableList<ColorItem> currentlySelectedColorItems) {
        _currentlySelectedColorItems = currentlySelectedColorItems;
    }

    /**
     * This function resets both color item / names list when the main menu button is pressed
     */

    public void resetCurrentlySelectedList() {
        _currentlySelectedColorItems = FXCollections.observableArrayList();
        _currentlySelectedNames = FXCollections.observableArrayList();
    }

    /**
     * This function makes custom names when the next button is pressed from practice choose -> practice mode
     */

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

    }

    /**
     * This function clears all the currently selected custom names and reset
     */

    public void clearCustomNames() {
        _currentlySelectedNames.clear();
    }

    /**
     * This function saves the existing hashmap which contains data of existing custom names
     */

    public void saveMap() {

        closeSession();

        Map<String, CustomNameSerializable> _serializableCustomNameMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        ArrayList<CustomNameSerializable> cnsList = new ArrayList<>();

        for (String s : _allCustomNames.keySet()) {
            System.out.println(s);

            CustomName cn = _allCustomNames.get(s);

            CustomNameSerializable cns = new CustomNameSerializable(
                    cn.getName(),
                    cn.getDir(),
                    cn.getListOfNames(),
                    cn.getALOfAttempts(),
                    cn.getCurrentAttemptNumber()
            );

            cnsList.add(cns);

            _serializableCustomNameMap.put(s, cns);
        }


        try {
            System.out.println("called");
            FileOutputStream fos = new FileOutputStream(".namesayer.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(cnsList);
            oos.close();
            fos.close();

            System.out.println(cnsList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function loads the .ser file and updates the hashmap on reload
     */

    public void openMap() {
        //Map<String, CustomNameSerializable> _temp = null;
        ArrayList<CustomNameSerializable> temp = null;

        File file = new File(".namesayer.ser");
        if (!file.exists()) {
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(".namesayer.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            temp = (ArrayList<CustomNameSerializable>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }

        System.out.println(temp);

        for (CustomNameSerializable cns : temp) {
            CustomName tempObj = new CustomName(cns);
            _allCustomNames.put(tempObj.getName(), tempObj);
        }

        ArrayList<CustomName> allVals = new ArrayList<>(_allCustomNames.values());
        _allCustomNamesList = FXCollections.observableArrayList(allVals);


    }

    /**
     * This function deletes a name and then removes all attempts
     */

    public void deleteName() {
        _selectedCustomName.deleteName();

        int index = 0;


        for (String s : _allCustomNames.keySet()) {
            if (s.equals(_selectedCustomName.getName())) {
                _allCustomNames.remove(s);
                break;
            }
        }


        for (CustomName s : _allCustomNamesList) {
            System.out.println(s.getName().equals(_selectedCustomName.getName()));
            if (s.getName().equals(_selectedCustomName.getName())) {
                _allCustomNamesList.remove(index);
                break;
            }

            index++;
        }

    }

    /**
     * This function deletes an attempt relating to a name
     */

    public void deleteAttempt() {
        int size = _selectedCustomName.deleteAttempt(_selectedAttempt);

        if (size == 0) {
            deleteName();
        }

    }


}
