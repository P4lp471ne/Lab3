package components.directoryView.remote;


import components.directoryControl.DirectoryControl;
import components.directoryView.DirectoryView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import manager.StringHelper;
import manager.SystemIconsProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DefaultRemoteDirectoryView extends ListView<String> implements DirectoryView {

    private String currentDirectory;
    private TextField textField;
    private ObservableList<String> childrenList;
    private DirectoryControl directoryControl;

    public DefaultRemoteDirectoryView(DirectoryControl control) throws IOException {
        super();
        this.directoryControl = control;

        childrenList = FXCollections.observableArrayList();
        setItems(childrenList);

        textField = new TextField();
        textField.setStyle("-fx-font-size: 10px;");

        setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    if (isFocused()) navigate(getSelectionModel().getSelectedItem());
                    break;
                case BACK_SPACE:
                    moveToParentDir();
                    break;
            }
        });

        setOnMouseClicked(m -> {
            if (m.getButton().equals(MouseButton.PRIMARY) && m.getClickCount() == 2)
                navigate(getSelectionModel().getSelectedItem());
        });
        setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                return new SystemIconsProvider.AttachmentListCell(DefaultRemoteDirectoryView.this);
            }
        });
        refresh();
    }

    public void refresh() {
        showList(getCurrentFilesList());
        textField.setText(currentDirectory);
    }

    public TextField getTextField() {
        return textField;
    }

    public List<String> getSelection() {
        return new ArrayList<>(getSelectionModel().getSelectedItems());
    }

    public String getDirectory() {
        return currentDirectory;
    }

    public void select(String regex) {
        if (regex.startsWith("*")) regex = "." + regex;
        getSelectionModel().clearSelection();
        for (int i = 0; i < childrenList.size(); ++i) {
            String item = childrenList.get(i);
            if (item.matches(regex) || StringHelper.containsWord(item, regex)) {
                getSelectionModel().select(i);
            }
        }
    }

    @Override
    public String getSelectedFile() {
        return null;
    }

    private String[] getCurrentFilesList() {
        List<String> files = directoryControl.ls();
        files.add(0, "..");

        return files.toArray(new String[0]);
    }

    private void showList(String[] list) {
        if (list != null) {
            childrenList.clear();
            childrenList.addAll(list);
        } else {
            childrenList.clear();
        }
    }

    public void openFile(String path) {
            directoryControl.openFile(path);
    }

    private void navigate(String name) {
        directoryControl.openFile(name);
        currentDirectory = directoryControl.getWorkingDirectory();
        refresh();
    }

    private void moveToParentDir() {
        directoryControl.openFile("..");
    }
}

