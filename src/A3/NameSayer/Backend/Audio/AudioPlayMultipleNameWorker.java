package A3.NameSayer.Backend.Audio;

import A3.NameSayer.Backend.Databases.Database;
import A3.NameSayer.Backend.Items.DatabaseName;
import javafx.concurrent.Task;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AudioPlayMultipleNameWorker extends Task<Integer> {

    public static Process pb;
    private Button _button;
    private List<DatabaseName> _listOfNames;

    public AudioPlayMultipleNameWorker(List<DatabaseName> listOfNames, Button button) {
        _listOfNames = listOfNames;
        _button = button;
    }

    @Override
    protected Integer call() throws Exception {
        for (DatabaseName db : _listOfNames) {
            String trimCommand = String.format(
                    "ffmpeg -y -hide_banner -i  " + "'" + db.getPathToRecording()
                            + "'" + " -af silenceremove=0:0:0:1:5:-30dB "
                            + "'" + System.getProperty("user.dir")
                            + "/Temp/temp.wav" + "'"
            );
            System.out.println(trimCommand);
            pb = new ProcessBuilder("bash", "-c", trimCommand).start();
            if (pb.waitFor() != 0) {
                return 1;
            }
            String ffmpegCommand = String.format("ffplay -nodisp -autoexit \'%s\'", System.getProperty("user.dir") + "/Temp/temp.wav");
            System.out.println(ffmpegCommand);
            try {
                pb = new ProcessBuilder("bash", "-c", ffmpegCommand).start();
                if (pb.waitFor() != 0) {
                    return 2;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    @Override
    protected void succeeded() {
        _button.setText("Listen");
    }

}

