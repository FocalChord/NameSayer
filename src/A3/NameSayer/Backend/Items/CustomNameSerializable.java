package A3.NameSayer.Backend.Items;

import javafx.collections.ObservableList;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class CustomNameSerializable implements Serializable {

    private String _name;
    private File _directory;
    private List<DatabaseName> _listOfNames;
    private List<Attempt> _listOfAttempts;
    private int _currentAttemptNumber;

    public CustomNameSerializable(String name, File directory, List<DatabaseName> listOfNames, List<Attempt> listOfAttempts, int currentAttemptNumber) {
        _name = name;
        _directory = directory;
        _listOfNames = listOfNames;
        _listOfAttempts = listOfAttempts;
        _currentAttemptNumber = currentAttemptNumber;
    }

    public String getName() {
        return _name;
    }

    public File getDirectory() {
        return _directory;
    }

    public List<DatabaseName> getListOfNames() {
        return _listOfNames;
    }

    public List<Attempt> getListOfAttempts() {
        return _listOfAttempts;
    }

    public int getCurrentAttemptNumber() {
        return _currentAttemptNumber;
    }




}
