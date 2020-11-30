package utils.strategies;

import components.directoryControl.DirectoryControl;
import javafx.scene.control.Alert;
import org.apache.commons.io.FileUtils;
import utils.DialogProvider;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileControl {

    public static void copy(DirectoryControl source, DirectoryControl destination) {
        if (source.isLocal() && destination.isLocal()) {
            localCopy(source, destination);
        } else {
            int fails = 0;
            for (String path : source.getSelection()) {
                try {
                    destination.saveFile(source.getFileStream(path), path);
                } catch (Exception e) {
                    fails++;
                }
                if (fails > 0) {
                    String message = "at least some files were not copied properly";
                    DialogProvider.showAlert(Alert.AlertType.INFORMATION, source.getWorkingDirectory(),
                            message, "");
                }
            }
        }
    }

    private static void localCopy(DirectoryControl source, DirectoryControl destination) {
        List<Path> uncopiable = new ArrayList<>();
        for (String path : source.getSelection()) {
            try {
                File sourceFile = Paths.get(path).toFile();
                if (sourceFile.isDirectory()) {
                    FileUtils.copyDirectoryToDirectory(sourceFile,
                            Paths.get(destination.getWorkingDirectory()).toFile());
                } else {
                    FileUtils.copyFileToDirectory(new File(source.getWorkingDirectory() + File.separator + sourceFile),
                            Paths.get(destination.getWorkingDirectory()).toFile());
                }
            } catch (Exception e) {
                uncopiable.add(Paths.get(path));
            }
        }
        if (uncopiable.size() > 0) {
            String sourceDirectory = uncopiable.get(0).getParent().toString();
            StringBuilder content = new StringBuilder();
            for (Path path : uncopiable) {
                content.append(path.toString()).append(System.lineSeparator());
            }
            String message = "Some files were not copied properly";
            DialogProvider.showAlert(Alert.AlertType.INFORMATION, sourceDirectory, message, content.toString());
        }
    }

    public static void move(DirectoryControl source, DirectoryControl destination) {
        if (source.isLocal() && destination.isLocal()) {
            localMove(source, destination);
            return;
        }
        int fails = 0;
        for (String path : source.getSelection()) {
            try {
                destination.saveFile(source.getFileStream(path), path);
            } catch (Exception e) {
                fails++;
            }
            if (fails > 0) {
                String message = "at least some files were not copied properly";
                DialogProvider.showAlert(Alert.AlertType.INFORMATION, source.getWorkingDirectory(),
                        message, "");
            }
        }
    }

    public static void localMove(DirectoryControl source, DirectoryControl destination) {
        List<Path> unmovable = new ArrayList<>();
        for (String path : source.getSelection()) {
            try {
                FileUtils.moveToDirectory(Paths.get(source.getWorkingDirectory() + File.separator + path).toFile(),
                        Paths.get(destination.getWorkingDirectory()).toFile(), false);
            } catch (Exception e) {
                unmovable.add(Paths.get(path));
            }
        }
        if (unmovable.size() > 0) {
            String sourceDirectory = String.valueOf(unmovable.get(0));
            StringBuilder content = new StringBuilder();
            for (Path path : unmovable) {
                content.append(path.toString()).append(System.lineSeparator());
            }
            String message = "Some files were not moved properly";
            DialogProvider.showAlert(Alert.AlertType.INFORMATION, sourceDirectory, message, content.toString());
        }
    }

    public static void delete(DirectoryControl source) {
        boolean isConfirmed = DialogProvider.showExpandableConfirmationDialog(source.getWorkingDirectory(), "Delete",
                "Do you really want to delete selected files?", source.getView().getSelection().toString());

        if (isConfirmed) {
            List<String> undeleted = source.delete(source.getView().getSelection());
            if (undeleted.size() > 0) {
                StringBuilder content = new StringBuilder();
                for (String path : undeleted) content.append(path).append(System.lineSeparator());
                String message = "Some files were not deleted";
                DialogProvider.showAlert(Alert.AlertType.INFORMATION, source.getWorkingDirectory(),
                        message, content.toString());
            }
        }
    }

    public static void createDirectory(DirectoryControl source) {
        String title = source.getWorkingDirectory();
        String name = DialogProvider.showTextInputDialog(title, null, "New Directory", "My Directory");
        if (name != null) {
            try {
                source.createDirectory(name);
            } catch (FileAlreadyExistsException e) {
                DialogProvider.showAlert(Alert.AlertType.INFORMATION, title,
                        "Directory already exists", source.getWorkingDirectory() + name);
            } catch (Exception e) {
                DialogProvider.showAlert(Alert.AlertType.INFORMATION, title,
                        "Directory was not created", source.getWorkingDirectory() + name);
            }
        }
    }

    public static void createFile(DirectoryControl source) {
        String title = source.getWorkingDirectory();
        String name = DialogProvider.showTextInputDialog(title, null, "New File", "Text File.txt");
        if (name != null) {
            try {
                source.createDirectory(name);
            } catch (FileAlreadyExistsException e) {
                DialogProvider.showAlert(Alert.AlertType.INFORMATION, title,
                        "File already exists", source.getWorkingDirectory() + name);
            } catch (Exception e) {
                DialogProvider.showAlert(Alert.AlertType.INFORMATION, title,
                        "File was not created", source.getWorkingDirectory() + name);
            }
        }
    }

    public static void rename(String source) {
        String title = "Rename";
        String name = DialogProvider.showTextInputDialog(title, null,
                "Enter New Name", source);
        if (name != null) {
            Path destination = Paths.get(source).getParent().resolve(name);
            try {
                FileUtils.moveToDirectory(Paths.get(source).toFile(), destination.toFile(), true);
            } catch (Exception e) {
                DialogProvider.showAlert(Alert.AlertType.INFORMATION, Paths.get(source).getParent().toString(),
                        "File was not renamed", source);
            }
        }
    }
}
