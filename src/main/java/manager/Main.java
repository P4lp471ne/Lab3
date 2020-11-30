package manager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private static final KeyCombination SHORTCUT_COPY = new KeyCodeCombination(KeyCode.F5);
    private static final KeyCombination SHORTCUT_MOVE = new KeyCodeCombination(KeyCode.F6);
    private static final KeyCombination SHORTCUT_DELETE = new KeyCodeCombination(KeyCode.DELETE);
    private static final KeyCombination SHORTCUT_NEW_FILE = new KeyCodeCombination(KeyCode.N,
            KeyCombination.CONTROL_DOWN);
    private static final KeyCombination SHORTCUT_NEW_DIRECTORY = new KeyCodeCombination(KeyCode.N,
            KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
    private static final KeyCombination SHORTCUT_RENAME = new KeyCodeCombination(KeyCode.F6, KeyCombination.SHIFT_DOWN);
    private static final KeyCombination SHORTCUT_FOCUS_TEXT_FIELD = new KeyCodeCombination(KeyCode.D, KeyCombination.SHIFT_DOWN);
    private static final KeyCombination SHORTCUT_HTML_EDITOR = new KeyCodeCombination(KeyCode.F3);
    private static final KeyCombination SHORTCUT_FTP = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
    private static final KeyCombination SHORTCUT_SFTP = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);

    private FileView fileView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();

        fileView = new FileView();

        VBox.setVgrow(fileView, Priority.ALWAYS);

        root.getChildren().addAll(getMenuBar(), fileView, getToolBar());

        Scene scene = new Scene(root, 840, 600);

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (SHORTCUT_DELETE.match(e)) {
                fileView.delete();
            } else if (SHORTCUT_NEW_FILE.match(e)) {
                fileView.createFile();
            } else if (SHORTCUT_FTP.match(e)) {
                fileView.ftp();
            } else if (SHORTCUT_SFTP.match(e)) {
                fileView.sftp();
            } else if (SHORTCUT_NEW_DIRECTORY.match(e)) {
                fileView.createDirectory();
            } else if (SHORTCUT_RENAME.match(e)) {
                fileView.rename();
            } else if (SHORTCUT_COPY.match(e)) {
                fileView.copy();
            } else if (SHORTCUT_MOVE.match(e)) {
                fileView.move();
            } else if (SHORTCUT_FOCUS_TEXT_FIELD.match(e)) {
                fileView.focusTextField();
            } else if (SHORTCUT_HTML_EDITOR.match(e)) {
                fileView.openHtml();
            }
        });

        primaryStage.setTitle("File Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar getMenuBar() {
        Menu fileMenu = new Menu("File");

        MenuItem newFile = new MenuItem("New File");
        newFile.setOnAction(e -> fileView.createFile());
        newFile.setAccelerator(SHORTCUT_NEW_FILE);

        MenuItem newFolder = new MenuItem("New Folder");
        newFolder.setOnAction(e -> fileView.createDirectory());
        newFolder.setAccelerator(SHORTCUT_NEW_DIRECTORY);

        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction(e -> fileView.rename());
        renameItem.setAccelerator(SHORTCUT_RENAME);

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> fileView.delete());
        deleteItem.setAccelerator(SHORTCUT_DELETE);

        fileMenu.getItems().addAll(newFile, newFolder, renameItem, deleteItem);
        return new MenuBar(fileMenu);
    }

    private ToolBar getToolBar() {
        Label labelOpenAsText = new Label("F3 Edit HTML");
        labelOpenAsText.setOnMouseClicked(e -> fileView.openHtml());

        Label labelCopy = new Label("F5 Copy");
        labelCopy.setOnMouseClicked(e -> fileView.copy());

        Label labelMove = new Label("F6 Move");
        labelMove.setOnMouseClicked(e -> fileView.move());

        return new ToolBar(labelOpenAsText, new Separator(), labelCopy, new Separator(), labelMove);
    }
}
