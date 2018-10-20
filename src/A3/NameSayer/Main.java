package A3.NameSayer;

import A3.NameSayer.Backend.RatingSystem.TextFileRW;
import A3.NameSayer.Backend.Switch.SwitchStage;
import A3.NameSayer.Backend.Databases.Database;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    public static long startTime = System.nanoTime();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        SwitchStage.getInstance().setPrivateStage(primaryStage);
        // Load database and textfiles
        TextFileRW.getInstance();
        Database.getInstance();

        // Load fonts
        Font.loadFont(getClass().getResourceAsStream("/A3/NameSayer/Frontend/Resources/Fonts/yugothil.ttf"), 65);
        Font.loadFont(getClass().getResourceAsStream("/A3/NameSayer/Frontend/Resources/Fonts/Cambria.ttf"), 72);

        // Set stage

        Parent root = FXMLLoader.load(getClass().getResource("Frontend/FXML/MainMenu.fxml"));
        primaryStage.setTitle("Name Sayer");
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        //JFXDecorator decorator = new JFXDecorator(primaryStage, root);
        //decorator.setCustomMaximize(true);
        primaryStage.setScene(new Scene(root, 1336, 768));

        primaryStage.setOnHiding(event -> Platform.runLater(() -> {

            TextFileRW.getInstance().onAppClose();
            //TextFileRW.getInstance().saveTime();

        }));
        primaryStage.show();
    }
}
