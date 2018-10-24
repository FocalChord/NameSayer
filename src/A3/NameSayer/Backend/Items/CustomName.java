package A3.NameSayer.Backend.Items;

import A3.NameSayer.Backend.Databases.Database;
import A3.NameSayer.Backend.Databases.UserDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is to deal with concatenated names where users can enter their custom names
 * i.e. Catherine Watson
 */

public class CustomName implements Serializable {

    private String _name; // Name of the CustomaName
    private File _directory; // Path to the directory
    private List<DatabaseName> _listOfNames; // This list contains a list of Database Names corresponding to the overall name
    private ObservableList<Attempt> _listOfAttempts; //  This list contains a list of all attempts relating to the custom name
    private int _currentAttemptNumber; // Stores the current attempt number for the custom name
    private String _recordingPath; // Stores the current recording path for the current attempt

    // This constructor is for making a new custom name
    public CustomName(String name) {
        _name = name;
        _directory = makeCustomNameDirectory();
        _listOfNames = splitCustomName();
        _listOfAttempts = initializeAttemptList();
        _currentAttemptNumber = 1;
    }

    // This constructor is for reading in a customNameSerializable object
    public CustomName(CustomNameSerializable obj) {
        _name = obj.getName();
        _directory = obj.getDirectory();
        _listOfNames = obj.getListOfNames();
        _listOfAttempts = FXCollections.observableArrayList(obj.getListOfAttempts());
        _currentAttemptNumber = obj.getCurrentAttemptNumber();
    }

    // This method makes a custom directory for each customName Object
    private File makeCustomNameDirectory() {
        String newFolderPath = UserDatabase.PATH_TO_USERS_FOLDER + "/" + _name;
        File dir = new File(newFolderPath);

        if (!dir.exists()) {
            dir.mkdir();
        }

        return dir;
    }

    // This method splits a string into database name objects (i.e. Catherine Watson -> Catherine and Watson)
    private List<DatabaseName> splitCustomName() {
        String name = _name;
        String[] splitNames = name.trim().split("[\\s-]+");

        List<DatabaseName> out = new ArrayList<>();

        for (String s : splitNames) {
            DatabaseName db = Database.getInstance().getDatabaseObj(s);
            out.add(db);
        }

        return out;
    }

    // This constructor creates an empty attempt list
    private ObservableList<Attempt> initializeAttemptList() {
        ObservableList<Attempt> out = FXCollections.observableArrayList();
        Attempt attempt = new Attempt(UserDatabase.NO_ATTEMPTS, "");
        out.add(attempt);
        return out;
    }

    // This method is called when the user presses delete after recording
    public void deleteCurrentAttempt() {
        File tempAudioFile = new File(_recordingPath);
        tempAudioFile.delete();
    }

    // This method is called when the user presses keep after recording
    public void keepCurrentAttempt() {
        Attempt attempt = new Attempt("Attempt " + _currentAttemptNumber, _directory.getAbsolutePath());

        if (_currentAttemptNumber == 1) {
            _listOfAttempts.clear();
        }

        _listOfAttempts.add(attempt);
        _currentAttemptNumber++;
        _recordingPath = "";
    }

    // This method is called to get the new attempt path for when the user begins to practice
    public String startNewAttempt() {
        Attempt attempt = new Attempt("Attempt " + _currentAttemptNumber, _directory.getAbsolutePath());
        _recordingPath = attempt.getAttemptPath();
        return _recordingPath;
    }

    // This method outputs true if there are no attempts and false is there are
    public boolean noAttempts() {
        if (_listOfAttempts.size() == 0) {
            return false;
        }

        return (_listOfAttempts.get(0).getAttemptName().equals(UserDatabase.NO_ATTEMPTS));
    }

    // This method is called when the user tries to delete a name in the database screen
    public void deleteName() {
        for (Attempt attempt : _listOfAttempts) {
            File file = new File(attempt.getAttemptPath());
            file.delete();
        }

        _listOfAttempts = FXCollections.observableArrayList();
        _directory.delete();
    }

    // This method is called when the user tries to deletes the attempt
    public int deleteAttempt(Attempt attemptToDelete) {
        File file = new File(attemptToDelete.getAttemptPath());
        file.delete();

        _listOfAttempts.removeIf(e -> e.getAttemptName().equals(attemptToDelete.getAttemptName()));

        return _listOfAttempts.size();

    }

    // All these getters get the fields

    public String getName() {
        return _name;
    }

    public File getDir() {
        return _directory;
    }

    public List<DatabaseName> getListOfNames() {
        return _listOfNames;
    }

    public ObservableList<Attempt> getListOfAttempts() {
        return _listOfAttempts;
    }

    public List<Attempt> getALOfAttempts() {
        return new ArrayList<>(_listOfAttempts);
    }

    public int getCurrentAttemptNumber() {
        return _currentAttemptNumber;
    }

}
