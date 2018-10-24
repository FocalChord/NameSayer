package A3.NameSayer.Backend.Items;

import java.io.Serializable;

/**
 * This class is to take the "Attempts" idea and make it a class, each attempt has a name and a path to the recording
 */
public class Attempt implements Serializable {
    private String _attemptName;
    private String _pathToAttempt;

    public Attempt(String attemptName, String pathToAttempt) {
        _attemptName = attemptName;
        _pathToAttempt = pathToAttempt;
    }

    public String getAttemptName() {
        return _attemptName;
    }

    public String getAttemptPath() {
        return _pathToAttempt + "/" + _attemptName + ".wav";
    }
}
