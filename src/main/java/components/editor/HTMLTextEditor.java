package components.editor;

import components.directoryControl.DirectoryControl;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import utils.DialogProvider;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class HTMLTextEditor extends HTMLEditor implements Editor {

    private Stage stage;
    private File file;
    private DirectoryControl directoryControl;

    public HTMLTextEditor(DirectoryControl directoryControl) {

        VBox root = new VBox();

        Menu fileMenu = new Menu("File");
        MenuItem saveHtmlMenuItem = new MenuItem("Save as HTML");
        saveHtmlMenuItem.setOnAction(e -> save());
        MenuItem closeMenuItem = new MenuItem("Close");
        closeMenuItem.setOnAction(e -> close());
        fileMenu.getItems().addAll(saveHtmlMenuItem, closeMenuItem);

        root.getChildren().addAll(new MenuBar(fileMenu), this);

        stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    public void open(File file) {
        if (file.toString().endsWith(".html")) {
            try {
                String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                this.file = file;
                setHtmlText(content);
                stage.setTitle(file.getPath());
                stage.show();
            } catch (IOException e) {
                this.file = null;
                stage.close();
            }
        }
    }

    public void save() {
        if (file != null) {
            try (PrintWriter printWriter = new PrintWriter(file)) {
                printWriter.write(getHtmlText());
                directoryControl.saveFile(new FileInputStream(file), file.getName());
            } catch (Exception e) {
                file = null;
            }
        }
    }

    public void save(File destination) {
        if (destination != null) {
            try (PrintWriter printWriter = new PrintWriter(destination)) {
                printWriter.write(getHtmlText());
            } catch (Exception ignored) {
            }
        }
    }

    public void close() {
        boolean save = DialogProvider.showConfirmationDialog(file.getName(), null, "Save changes?");
        if (save) save();
        stage.hide();
    }
}
