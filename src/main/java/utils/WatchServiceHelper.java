package utils;

import components.directoryView.DirectoryView;
import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class WatchServiceHelper {

    private WatchService watchService;
    private WatchKey watchKey;
    private volatile Thread watchThread;

    private DirectoryView directoryView;
    private Path currentDirectory;

    public WatchServiceHelper(DirectoryView directoryView) {
        this.directoryView = directoryView;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            watchKey = Paths.get(directoryView.getDirectory()).register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            currentDirectory = Paths.get(directoryView.getDirectory());
        } catch (IOException e) {
            DialogProvider.showException(e);
        }
        watchThread = new Thread(() -> {
            while (true) {
                try {
                    WatchKey watchKey = watchService.take();
                    watchKey.pollEvents();
                    updateUI();
                    watchKey.reset();
                } catch (InterruptedException e) {
                    DialogProvider.showException(e);
                }
            }
        });
        watchThread.start();
    }

    public void changeObservableDirectory(Path newDirectory) {
        if (currentDirectory.equals(newDirectory)) return;
        watchKey.cancel();
        try {
            watchKey = newDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            currentDirectory = newDirectory;
        } catch (IOException e) {
            DialogProvider.showException(e);
        }
    }

    private void updateUI() {
        Platform.runLater(() -> directoryView.refresh());
    }
}
