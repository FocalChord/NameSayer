package A3.NameSayer.Backend.Audio;

import javafx.concurrent.Task;
import javafx.scene.control.Button;

import java.io.IOException;

public class AudioPlayAttemptWorker extends Task<Integer> {

    public static Process pb;
    private String _attemptPath;
    private Button _button;

    public AudioPlayAttemptWorker(String attemptPath, Button button) {
        _attemptPath = attemptPath;
        _button = button;
    }

    @Override
    protected Integer call() throws Exception {
        String ffmpegCommand = String.format("ffplay -nodisp -autoexit \'%s\'", _attemptPath);
        System.out.println(ffmpegCommand);

        try {
            pb = new ProcessBuilder("bash", "-c", ffmpegCommand).start();
            if (pb.waitFor() != 0) {
                return 1;
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
