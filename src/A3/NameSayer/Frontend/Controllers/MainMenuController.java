package A3.NameSayer.Frontend.Controllers;

import A3.NameSayer.Backend.Switch.SwitchStage;
import A3.NameSayer.Backend.Switch.SwitchTo;
import A3.NameSayer.Backend.Switch.SwitchScenes;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class MainMenuController  implements Initializable  {


    @FXML
    private Button practiceButton;

    @FXML
    private Button databaseButton;

    @FXML
    private Button soundCheckButton;

    @FXML
    private Button statisticsButton;

    @FXML
    private ImageView helpView;

    @FXML
    private ImageView infoView;



    private WebView webView;

    private WebEngine webEngine;


    /**
     * Depending on which button is pressed, switches to the corresponding scene
     * @param e
     * @throws IOException
     */

    public void buttonClick(ActionEvent e) throws IOException {

        // Switch Scene to go to required scene

        if (e.getSource().equals(practiceButton)) {
            SwitchScenes.getInstance().switchScene(SwitchTo.PRACTICECHOOSE, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
        }

        if (e.getSource().equals(databaseButton)) {
            SwitchScenes.getInstance().switchScene(SwitchTo.DB, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
        }

        if (e.getSource().equals(soundCheckButton)) {
            SwitchScenes.getInstance().switchScene(SwitchTo.SOUNDCHECK, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);

        }
        if (e.getSource().equals(statisticsButton)) {
            SwitchScenes.getInstance().switchScene(SwitchTo.STATISTICS, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
        }
    }

    /**
     * When the info icon is hovered over, it changes colour
     */
    @FXML
    private void onMouseEnterInfo(){
        Image image = new Image("A3/NameSayer/Frontend/Resources/Images/InfoInvert.png");
        infoView.setImage(image);
    }

    /**
     * Makes info icon default colour when un-hovered
     */
    @FXML
    private void onMouseExitInfo(){
        Image image = new Image("A3/NameSayer/Frontend/Resources/Images/Info.png");
        infoView.setImage(image);
    }

    /**
     * When help icon is hovered over, it changes colour
     */

    @FXML
    private void onMouseEnterHelp() {
        Image image = new Image("A3/NameSayer/Frontend/Resources/Images/HelpInvert.png");
        helpView.setImage(image);

    }

    /**
     * Makes help icon default colour when un-hovered
     */

    @FXML
    private void onMouseExitHelp(){
        Image image = new Image("A3/NameSayer/Frontend/Resources/Images/Help.png");
        helpView.setImage(image);
    }

    /**
     * When info icon is pressed, opens the information screen
     * @throws IOException
     */

    @FXML
    private void onMouseClickInfo() throws IOException {

        URL url = getClass().getClassLoader().getResource("A3/NameSayer/Frontend/FXML/Information.fxml");

        FXMLLoader loader = new FXMLLoader(url);

        Parent sParent = loader.load();
        Scene sScene = new Scene(sParent, 600, 300);
        Stage stage = new Stage();
        stage.setScene(sScene);
        stage.show();
    }

    /**
     * When help icon is pressed, opens the help screen
     */

    @FXML
    private void onMouseClickHelp(){
       Stage stage = new Stage();
       File file = new File("ManualHTML/UserManual.html").getAbsoluteFile();
       webView = new WebView();
       webEngine = webView.getEngine();
       webEngine.load(file.toURI().toString());
       Scene scene = new Scene(webView,800,600);
       stage.setScene(scene);
       stage.show();

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}

