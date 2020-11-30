package components.directoryControl;

import components.directoryView.DirectoryView;
import javafx.scene.Node;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface DirectoryControl {
    void createFile(String path) throws IOException;

    void createDirectory(String path) throws IOException;

    void rename(String s) throws IOException;

    FileInputStream getFileStream(String path) throws FileNotFoundException;

    void openFile(String file);

    List<String> delete(List<String> files);

    List<String> getSelection();

    DirectoryView getView();

    String getWorkingDirectory();

    boolean isLocal();

    List<String> ls();

    void saveFile(FileInputStream fis, String name);

    void setView(DirectoryView view);
}
