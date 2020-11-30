package components.directoryControl.local;

import components.directoryControl.DirectoryControl;
import components.directoryView.DirectoryView;
import components.directoryView.local.DefaultLocalDirectoryView;
import utils.DialogProvider;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class DefaultLocalDirectoryControl implements DirectoryControl {
    DirectoryView directoryView;
    File currentDir;

    public DefaultLocalDirectoryControl(String startDir) {
        currentDir = new File(startDir);
        directoryView = new DefaultLocalDirectoryView(this);
    }

    @Override
    public void createFile(String path) throws IOException {
        Files.createFile(Paths.get(currentDir + path));
    }

    @Override
    public void createDirectory(String path) throws IOException {
        Files.createDirectory(Paths.get(currentDir + File.separator + path));
    }

    @Override
    public void rename(String s) {
        new File(directoryView.getSelectedFile()).renameTo(new File(s));
    }

    @Override
    public FileInputStream getFileStream(String path) throws FileNotFoundException {
        return new FileInputStream(currentDir + File.separator + path);
    }

    @Override
    public void openFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            directoryView.refresh();
            return;
        }
        if (file.isDirectory()) {
            currentDir = file;
            directoryView.refresh();
        } else if (file.isFile()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception e) {
                DialogProvider.showException(e);
            }
        }
    }

    @Override
    public List<String> delete(List<String> files) {
        List <String> result = new ArrayList<>();
        for (String file:
             files) {
            if (!(new File(currentDir.getPath() + File.separator + file)).delete()) result.add(file);
        }
        return result;
    }

    @Override
    public List<String> getSelection() {
        return directoryView.getSelection();
    }

    @Override
    public DirectoryView getView() {
        return directoryView;
    }

    @Override
    public String getWorkingDirectory() {
        return currentDir.getPath();
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public List<String> ls() {
        File[] files = currentDir.listFiles(file -> !file.isHidden());
        List<String> result = new LinkedList<>();
        try {
            assert files != null;
            Map<String, Boolean> data = new HashMap<>();
            for (File f:
                 files) {
                data.put(f.getName(), f.isDirectory());
                result.add(f.getName());
            }
            Collections.sort(result, (f1, f2) -> {
                if (data.get(f1).equals(data.get(f2))) {
                    return ((String)f1).compareTo((String) f2);
                }
                return data.get(f1) ? -1 : 1;
            });
            return result;
        } catch (Exception e) {
            DialogProvider.showException(e);
            e.printStackTrace();
        }
        for (File file : files) result.add(file.getName());
        return result;
    }

    @Override
    public void saveFile(FileInputStream fis, String name) {
        try {
            File myObj = new File(currentDir + File.separator + name);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName() + myObj.getAbsolutePath());
                OutputStream os = new FileOutputStream(name);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                fis.close();
                os.flush();
                os.close();
            } else {
                DialogProvider.showException(new Exception("File already exists."));
            }

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void setView(DirectoryView view) {
        this.directoryView = view;
    }
}
