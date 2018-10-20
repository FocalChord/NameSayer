package A3.NameSayer.Backend.Items;

public class Attempt {
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
