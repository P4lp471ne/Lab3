package manager;

import components.directoryView.DirectoryView;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SystemIconsProvider {

    static HashMap<String, Image> fileIconMap = new HashMap<String, Image>();

    private static String getFileExt(String filename) {
        String ext = ".";
        int p = filename.lastIndexOf('.');
        if (p >= 0) {
            ext = filename.substring(p);
        }
        return ext.toLowerCase();
    }

    private static javax.swing.Icon getJSwingIconFromFileSystem(File file) {

        FileSystemView view = FileSystemView.getFileSystemView();

        return view.getSystemIcon(file);
    }

    private static Image getFileIcon(String fname) {
        final String ext = getFileExt(fname);

        Image fileIcon = fileIconMap.get(ext);
        if (fileIcon == null) {

            javax.swing.Icon jswingIcon = null;

            File file = new File(fname);
            if (file.exists()) {
                jswingIcon = getJSwingIconFromFileSystem(file);
            } else {
                File tempFile = null;
                try {
                    tempFile = File.createTempFile("icon", ext);
                    jswingIcon = getJSwingIconFromFileSystem(tempFile);
                } catch (IOException ignored) {
                    // Cannot create temporary file.
                } finally {
                    if (tempFile != null) tempFile.delete();
                }
            }

            if (jswingIcon != null) {
                fileIcon = jswingIconToImage(jswingIcon);
                fileIconMap.put(ext, fileIcon);
            }
        }

        return fileIcon;
    }

    private static Image jswingIconToImage(javax.swing.Icon jswingIcon) {
        BufferedImage bufferedImage = new BufferedImage(jswingIcon.getIconWidth(), jswingIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static class AttachmentListCell extends ListCell<String> {
        private DirectoryView directoryView;

        public AttachmentListCell(DirectoryView directoryView) {
            this.directoryView = directoryView;
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                setText(null);
            } else {
                Image fxImage = getFileIcon(directoryView.getDirectory() + item);
                ImageView imageView = new ImageView(fxImage);
                setGraphic(imageView);
                setText(item);
            }
        }
    }
}
