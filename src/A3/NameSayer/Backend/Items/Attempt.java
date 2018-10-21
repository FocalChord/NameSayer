package A3.NameSayer.Backend.Items;

import java.io.Serializable;

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
