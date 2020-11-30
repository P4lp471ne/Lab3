package components.directoryView.local;

import components.directoryControl.DirectoryControl;
import components.directoryView.DirectoryView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import manager.StringHelper;
import manager.SystemIconsProvider;
import utils.WatchServiceHelper;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DefaultLocalDirectoryView extends ListView<String> implements DirectoryView {

    private TextField textField;
    private ObservableList<String> childrenList;
    private DirectoryControl control;

//    private WatchServiceHelper mWatchServiceHelper;

    public DefaultLocalDirectoryView(DirectoryControl control) {
        super();
        this.control = control;
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
            public ListCell<String> call(javafx.scene.control.ListView<String> list) {
                return new SystemIconsProvider.AttachmentListCell(DefaultLocalDirectoryView.this);
            }
        });
//        mWatchServiceHelper = new WatchServiceHelper(this);
        refresh();
    }

    public void refresh() {
        showList(getCurrentFilesList());
        textField.setText(control.getWorkingDirectory());
//        mWatchServiceHelper.changeObservableDirectory(Paths.get(control.getWorkingDirectory()));
    }

    public String getDirectory() {
        return control.getWorkingDirectory();
    }

    public TextField getTextField() {
        return textField;
    }

    public List<String> getSelection() {
        List<String> selection = new ArrayList<>(getSelectionModel().getSelectedItems());
        return selection;
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
        List<String> selection = getSelection();
        if (selection.size() == 1) return selection.get(0);
        return null;
    }

    private String[] getCurrentFilesList() {
//        File[] listFiles = new File(control.getWorkingDirectory()).listFiles(file -> !file.isHidden());
//
//        if (listFiles == null) {
//            listFiles = new File[0];
//        }
//
//        Arrays.sort(listFiles, (f1, f2) -> {
//            if ((f1.isDirectory() && f2.isDirectory()) || (f1.isFile() && f2.isFile())) {
//                return f1.compareTo(f2);
//            }
//            return f1.isDirectory() ? -1 : 1;
//        });
        List<String> files = control.ls();
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

    private void navigate(String name) {
        if (name.equals("..")) {
            moveToParentDir();
            return;
        }
        String selectedPath = control.getWorkingDirectory() + File.separator + name;
        control.openFile(selectedPath);
        refresh();
    }

    private void moveToParentDir() {
        String parent = new File(control.getWorkingDirectory()).getParentFile().getPath();
        control.openFile(parent);
        refresh();
    }
}
