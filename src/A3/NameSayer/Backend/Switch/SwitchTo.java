package A3.NameSayer.Backend.Switch;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

public enum SwitchTo {

    // Add Extra FXML Files here


    MAINMENU("A3/NameSayer/Frontend/FXML/MainMenu.fxml"),
    PRACTICESELECTION("A3/NameSayer/Frontend/FXML/PracticeSelection.fxml"),
    PRACTICEMODE("A3/NameSayer/Frontend/FXML/PracticeMode.fxml"),
    DB("A3/NameSayer/Frontend/FXML/Database.fxml"),
    SOUNDCHECK("A3/NameSayer/Frontend/FXML/SoundCheck.fxml"),
    RECORDSTAGE("A3/NameSayer/Frontend/FXML/PracticeRecord.fxml"),
    PRACTICEQUERY("A3/NameSayer/Frontend/FXML/PracticeQuery.fxml"),
    PRACTICEPICK("A3/NameSayer/Frontend/FXML/PracticePick.fxml"),
    PRACTICECHOOSE("A3/NameSayer/Frontend/FXML/PracticeChoose.fxml"),
    PRACTICEINPUT("A3/NameSayer/Frontend/FXML/PracticeInput.fxml"),
    STATISTICS("A3/NameSayer/Frontend/FXML/Stats.fxml"),
    INFO("A3/NameSayer/Frontend/FXML/INFORMATION.fxml");



    private URL _sceneURL;

    // Constructor to get the Scene URL

    SwitchTo(String sceneURL) {
        _sceneURL = getClass().getClassLoader().getResource(sceneURL);
    }

    // get the fxml loader


    public FXMLLoader getLoader() {
        return new FXMLLoader(_sceneURL);
    }

    // Might need in future code

    public Object getController() throws IOException {
        FXMLLoader loader = new FXMLLoader(_sceneURL);
        loader.load();
        return loader.getController();
    }


}
