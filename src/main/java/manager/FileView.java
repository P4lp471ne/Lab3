package manager;

import components.directoryControl.DirectoryControl;
import components.directoryView.DirectoryView;
import components.editor.HTMLTextEditor;
import components.pane.MultifuctionalPane;
import components.pane.Pane;
import components.pane.Type;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import utils.DialogProvider;
import utils.strategies.FileControl;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class FileView extends HBox {

    private static final String ACTION_SELECT = "select";
    private static final String ACTION_COPY = "copy";
    private static final String ACTION_MOVE = "move";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_OPEN = "open";

    private MultifuctionalPane leftPane;
    private MultifuctionalPane rightPane;

    private HTMLTextEditor htmlTextEditor;

    public FileView() {
        File[] roots = File.listRoots();
        String leftPanePath = roots[0].getPath();
        String rightPanePath = roots.length > 1 ? roots[1].getPath() : leftPanePath;

        leftPane = new MultifuctionalPane(leftPanePath);
        rightPane = new MultifuctionalPane(rightPanePath);
        update();
    }
    private void refresh(){
        leftPane.getView().refresh();
        rightPane.getView().refresh();
    }

    private void update(){
        leftPane.getView().getTextField().setOnAction(e -> onTextEntered(leftPane.getView().getTextField()));
        rightPane.getView().getTextField().setOnAction(e -> onTextEntered(rightPane.getView().getTextField()));

        VBox leftView = new VBox(leftPane.getView().getTextField(), (Node) leftPane.getView());
        VBox rightView = new VBox(rightPane.getView().getTextField(), (Node) rightPane.getView());
        ((Node) leftPane.getView()).setFocusTraversable(true);

        VBox.setVgrow((Node) leftPane.getView(), Priority.ALWAYS);
        VBox.setVgrow((Node) rightPane.getView(), Priority.ALWAYS);
        HBox.setHgrow(leftView, Priority.ALWAYS);
        HBox.setHgrow(rightView, Priority.ALWAYS);

        getChildren().setAll(leftView, rightView);
    }

    public void copy() {
        DirectoryControl left = leftPane.getDirectoryController();
        DirectoryControl right = rightPane.getDirectoryController();
        if((left == null) || (right == null)) return;
        if (leftPane.getView().isFocused()) {
            FileControl.copy(left, right);
        } else if (rightPane.getView().isFocused()) {
            FileControl.copy(right, left);
        }
        refresh();
    }

    public void move() {
        DirectoryControl left = leftPane.getDirectoryController();
        DirectoryControl right = rightPane.getDirectoryController();
        if((left == null) || (right == null)) return;
        if (leftPane.getView().isFocused()) {
            FileControl.move(left, right);
        } else if (rightPane.getView().isFocused()) {
            FileControl.move(right, left);
        }
        refresh();
    }

    public void delete() {
        DirectoryControl control = getFocusedPane().getDirectoryController();
        if (control != null) FileControl.delete(control);
        refresh();
    }

    public void rename() {
        Pane focusedPane = getFocusedPane();
        if (focusedPane != null) {
            List<String> selection = focusedPane.getView().getSelection();
            if (selection.size() == 1) FileControl.rename(selection.get(0));
        }
        refresh();
    }

    public void createDirectory() {
        DirectoryControl control = getFocusedPane().getDirectoryController();
        if (control != null) {
            FileControl.createDirectory(control);
        }
        refresh();
    }

    public void createFile() {
        DirectoryControl control = getFocusedPane().getDirectoryController();
        if (control != null) {
            FileControl.createFile(control);
        }
        refresh();
    }

    public void focusTextField() {
        DirectoryView focusedPane = Objects.requireNonNull(getFocusedPane()).getView();
        if (focusedPane != null) focusedPane.getTextField().requestFocus();
    }

    public void openHtml() {
        DirectoryControl control = getFocusedPane().getDirectoryController();
        htmlTextEditor = new HTMLTextEditor(control);
        if (control == null) return;
        File file = new File(control.getView().getSelectedFile());
        if (file.exists()) htmlTextEditor.open(file);
    }

    private Pane getFocusedPane() {
        if (leftPane.getView().isFocused() || leftPane.getView().getTextField().isFocused()) {
            return leftPane;
        } else if (rightPane.getView().isFocused() || rightPane.getView().getTextField().isFocused()) {
            return rightPane;
        } else {
            return null;
        }
    }

    private Pane getFocusedPane(TextField textField) {
        if (textField == leftPane.getView().getTextField()) {
            return leftPane;
        } else {
            return rightPane;
        }
    }

    private String getSelectedPath() {
        Pane focusedPane = getFocusedPane();
        if (focusedPane == null) return null;
        List<String> selection = focusedPane.getView().getSelection();
        if (selection.size() != 1) return null;
        return selection.get(0);
    }

    private void onTextEntered(TextField textField) {
        Pane focusedPane = getFocusedPane(textField);
        DirectoryView focusedPaneView = getFocusedPane(textField).getView();
        String command = textField.getText().trim();
        if (command.startsWith(ACTION_SELECT)) {
            String regex = command.substring(ACTION_SELECT.length()).trim();
            focusedPaneView.select(regex);
            focusedPaneView.requestFocus();
        } else if (command.startsWith(ACTION_COPY)) {
            String regex = command.substring(ACTION_COPY.length()).trim();
            focusedPaneView.select(regex);
            focusedPaneView.requestFocus();
            copy();
        } else if (command.startsWith(ACTION_MOVE)) {
            String regex = command.substring(ACTION_MOVE.length()).trim();
            focusedPaneView.select(regex);
            focusedPaneView.requestFocus();
            move();
        } else if (command.startsWith(ACTION_DELETE)) {
            String regex = command.substring(ACTION_DELETE.length()).trim();
            focusedPaneView.select(regex);
            focusedPaneView.requestFocus();
            delete();
        } else if (command.startsWith(ACTION_OPEN)) {
            String regex = command.substring(ACTION_OPEN.length()).trim();
            focusedPaneView.select(regex);
            focusedPaneView.requestFocus();
            for (String path : focusedPaneView.getSelection()) {
                try {
                    focusedPane.getDirectoryController().openFile(path);
                } catch (Exception e) {
                    DialogProvider.showException(e);
                }
            }
        } else {
            focusedPane.getDirectoryController().openFile(command);
            focusedPaneView.requestFocus();
        }
        textField.setText(focusedPane.getDirectoryController().getWorkingDirectory());
    }

    public void ftp() {
        Objects.requireNonNull(getFocusedPane()).setType(Type.FTP);
        update();
    }

    public void sftp() {
        Objects.requireNonNull(getFocusedPane()).setType(Type.SSH);
        update();
    }
}
