package A3.NameSayer.Backend.Switch;

import javafx.fxml.FXMLLoader;
import java.net.URL;

public enum SwitchTo {

    /**
     * The following enums are there to provide an easy way of getting the loader for the corresponding
     * fxml files
     */

    MAINMENU("A3/NameSayer/Frontend/FXML/MainMenu.fxml"),
    PRACTICEMODE("A3/NameSayer/Frontend/FXML/PracticeMode.fxml"),
    DB("A3/NameSayer/Frontend/FXML/Database.fxml"),
    SOUNDCHECK("A3/NameSayer/Frontend/FXML/SoundCheck.fxml"),
    RECORDSTAGE("A3/NameSayer/Frontend/FXML/PracticeRecord.fxml"),
    PRACTICEQUERY("A3/NameSayer/Frontend/FXML/PracticeQuery.fxml"),
    PRACTICECHOOSE("A3/NameSayer/Frontend/FXML/PracticeChoose.fxml"),
    STATISTICS("A3/NameSayer/Frontend/FXML/Stats.fxml");

    private URL _sceneURL;

    // Constructor to get the Scene URL

    SwitchTo(String sceneURL) {
        _sceneURL = getClass().getClassLoader().getResource(sceneURL);
    }

    /**
     * This method returns a FXML loader for the corresponding FXML file.
     *
     */
    public FXMLLoader getLoader() {
        return new FXMLLoader(_sceneURL);
    }

}
