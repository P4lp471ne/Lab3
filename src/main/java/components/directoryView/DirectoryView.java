package components.directoryView;

import javafx.scene.control.TextField;

import java.util.List;

public interface DirectoryView {
    void refresh();

    TextField getTextField();

    List<String> getSelection();

    void select(String regex);

    String getSelectedFile();

    void requestFocus();

    boolean isFocused();

    String getDirectory();
}
