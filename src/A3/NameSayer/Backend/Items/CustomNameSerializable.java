package A3.NameSayer.Backend.Items;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * This class exists since CustomName couldn't be serialized due to an observable list existing in a field
 * Hence this class works as a wrapper class for seralizing
 *
 * All of this class is self explanatory
 */
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
