package A3.NameSayer.Backend.Audio;

import javafx.concurrent.Task;
import javafx.scene.control.Button;
import java.io.IOException;

/**
 * AudioPlayAttemptWorker plays an attempt from the database, first trimming it and then playing
 */

public class AudioPlayAttemptWorker extends Task<Integer> {

    public static Process _pb;
    private String _attemptPath;
    private Button _button;

    public AudioPlayAttemptWorker(String attemptPath, Button button) {
        _attemptPath = attemptPath;
        _button = button;
    }

    @Override
    protected Integer call() throws Exception {
        //Trim audio
        String trimCommand = String.format(
                "ffmpeg -y -hide_banner -i  " + "'" + _attemptPath + "'" + " -af silenceremove=0:0:0:1:5:-35dB "
                        + "'" + System.getProperty("user.dir")
                        + "/Temp/temp.wav" + "'"
        );
        _pb = new ProcessBuilder("bash", "-c", trimCommand).start();
        if (_pb.waitFor() != 0) {
            return 1;
        }
        //Play audio
        String ffmpegCommand = String.format("ffplay -nodisp -autoexit \'%s\'", System.getProperty("user.dir") + "/Temp/temp.wav");

        try {
            _pb = new ProcessBuilder("bash", "-c", ffmpegCommand).start();
            if (_pb.waitFor() != 0) {
                return 2;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    protected void succeeded() {
        _button.setText("Listen");
    }
}
