package A3.NameSayer.Backend.Audio;

import A3.NameSayer.Backend.Items.DatabaseName;
import javafx.concurrent.Task;
import javafx.scene.control.Button;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Audio {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void playAudio(List<DatabaseName> listOfNames, Button button) {
        Task<Integer> audioPlayWorker = new AudioPlayMultipleNameWorker(listOfNames, button);
        executor.execute(audioPlayWorker);
    }

    public void playAttempt(String attemptPath, Button button) {
        Task<Integer> audioPlayAttemptWorker = new AudioPlayAttemptWorker(attemptPath, button);
        executor.execute(audioPlayAttemptWorker);
    }

    public void recordAudio(String filePath) {
        Thread recordAudio = new Thread(new AudioRecordWorker((filePath)));
        recordAudio.setDaemon(true);
        recordAudio.start();
    }

    public void compareRecordings(List<DatabaseName> listOfNames, String recordingPath, Button button) {
        Task<Integer> audioCompareWorker = new AudioCompareWorker(listOfNames, recordingPath, button);
        executor.execute(audioCompareWorker);
    }

}

