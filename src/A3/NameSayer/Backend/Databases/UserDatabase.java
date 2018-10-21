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
    private UserDatabase() {
        initialiseMaps();

    }

    // Putting the singleton in "singleton"
    public static UserDatabase getInstance() {
        if (_userDatabase == null) {
            _userDatabase = new UserDatabase();
        }

        return _userDatabase;
    }

    private void initialiseMaps() {

    }

    public CustomName getCurrentCustomName() {
        return _selectedCustomName;
    }

    public Attempt getCurrentAttempt() {
        return _selectedAttempt;
    }

    public void setCurrentAttempt(Attempt attempt) {
        _selectedAttempt = attempt;
    }

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

    }

    public void clearCustomNames() {
        _currentlySelectedNames.clear();
    }

    public void saveMap() {

        closeSession();

        System.out.println(_allCustomNames.size());

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

        for (CustomNameSerializable cns : temp) {
            CustomName tempObj = new CustomName(cns);
            _allCustomNames.put(tempObj.getName(), tempObj);
        }

        ArrayList<CustomName> allVals = new ArrayList<>(_allCustomNames.values());
        _allCustomNamesList = FXCollections.observableArrayList(allVals);


    }

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

    public void deleteAttempt() {
        int size = _selectedCustomName.deleteAttempt(_selectedAttempt);

        if (size == 0) {
            deleteName();
        }

    }


}
