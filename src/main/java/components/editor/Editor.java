package components.editor;

import java.io.File;

public interface Editor {
    void open(File file);

    void save(File file);

    void save();

    void close();

    void requestFocus();
//    void open(String path);
//    void save(String path);
}
