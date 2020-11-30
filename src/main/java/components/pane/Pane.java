package components.pane;

import components.directoryControl.DirectoryControl;
import components.directoryView.DirectoryView;
import javafx.scene.Node;

public interface Pane {
    void setType(Type type);
    DirectoryControl getDirectoryController();
    DirectoryView getView();
}
