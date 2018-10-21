package A3.NameSayer.Backend.Items;

import A3.NameSayer.Backend.Databases.Database;
import A3.NameSayer.Backend.Databases.UserDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CustomName implements Serializable {


    private String _name;
    private File _directory;
    private List<DatabaseName> _listOfNames;
    private ObservableList<Attempt> _listOfAttempts;
    private int _currentAttemptNumber;

    private String _recordingPath;

    public CustomName(String name) {
        _name = name;
        _directory = makeCustomNameDirectory();
        _listOfNames = splitCustomName();
        _listOfAttempts = initializeAttemptList();
        _currentAttemptNumber = 1;
    }

    public CustomName(CustomNameSerializable obj) {
        _name = obj.getName();
        _directory = obj.getDirectory();
        _listOfNames = obj.getListOfNames();
        _listOfAttempts = FXCollections.observableArrayList(obj.getListOfAttempts());
        _currentAttemptNumber = obj.getCurrentAttemptNumber();
    }

    private File makeCustomNameDirectory() {
        String newFolderPath = UserDatabase.PATH_TO_USERS_FOLDER + "/" + _name;
        File dir = new File(newFolderPath);

        if (!dir.exists()) {
            dir.mkdir();
        }

        return dir;
    }

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

    private ObservableList<Attempt> initializeAttemptList() {
        ObservableList<Attempt> out = FXCollections.observableArrayList();
        Attempt attempt = new Attempt(UserDatabase.NO_ATTEMPTS, "");
        out.add(attempt);
        return out;
    }

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

    public void deleteCurrentAttempt() {
        File tempAudioFile = new File(_recordingPath);
        tempAudioFile.delete();
    }

    public void keepCurrentAttempt() {
        Attempt attempt = new Attempt("Attempt " + _currentAttemptNumber, _directory.getAbsolutePath());

        if (_currentAttemptNumber == 1) {
            _listOfAttempts.clear();
        }

        _listOfAttempts.add(attempt);
        _currentAttemptNumber++;
        _recordingPath = "";
    }

    public String startNewAttempt() {
        Attempt attempt = new Attempt("Attempt " + _currentAttemptNumber, _directory.getAbsolutePath());
        _recordingPath = attempt.getAttemptPath();
        return _recordingPath;
    }

    public boolean noAttempts() {
        if (_listOfAttempts.size() == 0) {
            return false;
        }

        return (_listOfAttempts.get(0).getAttemptName().equals(UserDatabase.NO_ATTEMPTS));
    }

    public void deleteName() {
        for (Attempt attempt : _listOfAttempts) {
            File file = new File(attempt.getAttemptPath());
            file.delete();
        }

        _listOfAttempts = FXCollections.observableArrayList();
        _directory.delete();
    }

    public int deleteAttempt(Attempt attemptToDelete) {
        File file = new File(attemptToDelete.getAttemptPath());
        file.delete();

        _listOfAttempts.removeIf(e -> e.getAttemptName().equals(attemptToDelete.getAttemptName()));

        return _listOfAttempts.size();

    }
}
