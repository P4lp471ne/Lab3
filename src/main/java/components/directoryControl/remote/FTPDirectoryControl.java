package components.directoryControl.remote;

import components.directoryControl.DirectoryControl;
import components.directoryView.DirectoryView;
import components.directoryView.remote.DefaultRemoteDirectoryView;
import utils.DialogProvider;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FTPDirectoryControl implements DirectoryControl {
    DirectoryView directoryView;
    String currentDir;
    FTPClient ftpClient;

    public FTPDirectoryControl(String link) throws IOException, URISyntaxException {
        URI url = new URI(link);
        String[] userInfo = url.getUserInfo().split(":");
        ftpClient = new FTPClient();
        ftpClient.connect(url.getHost());
        ftpClient.login(userInfo[0], "");
        System.out.println(ftpClient.printWorkingDirectory());
        ftpClient.changeWorkingDirectory(url.getPath());
        currentDir = ftpClient.printWorkingDirectory();
        directoryView = new DefaultRemoteDirectoryView(this);
    }

    @Override
    public void createFile(String path) throws IOException {
        Path temp = Files.createTempFile(path, null);
        ftpClient.storeFile(path, new FileInputStream(String.valueOf(temp)));
    }

    @Override
    public void createDirectory(String path) throws IOException {
        ftpClient.makeDirectory(path);
    }

    @Override
    public void rename(String s) throws IOException {
        ftpClient.rename(directoryView.getSelectedFile(), s);
    }

    @Override
    public FileInputStream getFileStream(String path) throws FileNotFoundException {
        try {
            Path temp = Files.createTempFile(null, null);
            ftpClient.retrieveFile(path, new FileOutputStream(String.valueOf(temp)));
            return new FileInputStream(String.valueOf(temp));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void openFile(String path) {
        try {
            if (path.equals("..")) {
                ftpClient.changeToParentDirectory();
                currentDir = ftpClient.printWorkingDirectory();
                directoryView.refresh();
                return;
            }
            if (ftpClient.changeWorkingDirectory(path)) {
                currentDir = ftpClient.printWorkingDirectory();
                directoryView.refresh();
                return;
            }
            FTPFile file = ftpClient.mdtmFile(path);
            if (file != null) {
                try {
                    File temp = Files.createTempFile(null, null).toFile();
                    ftpClient.retrieveFile(path, new FileOutputStream(String.valueOf(temp)));
                    Desktop.getDesktop().open(temp);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    DialogProvider.showException(e);
                }
            }
        } catch (IOException e) {
            directoryView.refresh();
            DialogProvider.showException(e);
        }
    }

    @Override
    public List<String> delete(List<String> files) {
        List<String> result = new LinkedList<String>();
        for (String file : files) {
            try {
                if (!ftpClient.deleteFile(file)) {
                    result.add(file);
                }
            } catch (IOException e) {
                DialogProvider.showException(e);
            }
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
        return currentDir;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public List<String> ls() {
        try {
            FTPFile[] files = ftpClient.listFiles();
            List<String> result = new LinkedList<>();
            Arrays.sort(files, (f1, f2) -> {
                if ((f1.isDirectory() && f2.isDirectory()) || (f1.isFile() && f2.isFile())) {
                    return f1.getName().compareTo(f2.getName());
                }
                return f1.isDirectory() ? -1 : 1;
            });
            for (FTPFile f : files) result.add(f.getName());
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveFile(FileInputStream fis, String name) {
        try {
            if (ftpClient.storeFile(currentDir + File.separator + name, fis)) System.out.println("done");
        } catch (IOException e) {
            DialogProvider.showException(e);
        }
    }

    @Override
    public void setView(DirectoryView view) {
        this.directoryView = view;
    }
}
