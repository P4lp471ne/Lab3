package components.directoryControl.remote;

import com.jcraft.jsch.*;
import components.directoryControl.DirectoryControl;
import components.directoryView.DirectoryView;
import utils.DialogProvider;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SFTPDirectoryControl implements DirectoryControl {
    DirectoryView directoryView;
    String currentDir;
    Session session;
    Channel channel;
    ChannelSftp channelSftp;

    public SFTPDirectoryControl(String link) throws URISyntaxException, JSchException, SftpException {
        URI url = new URI(link);
        String[] info = url.getUserInfo().split(":");
        String SFTPUSER = info[0];
        String SFTPPASS = "";
        try {
            SFTPPASS = info[1];
        } catch (Exception ignored) {
        }
        String host = url.getHost();
        int port = url.getPort();
        String workingDir = url.getPath();

        JSch jsch = new JSch();
        session = jsch.getSession(SFTPUSER, host, port);
        session.setPassword(SFTPPASS);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        channel = session.openChannel("sftp");
        channel.connect();
        channelSftp = (ChannelSftp) channel;
        try {
            channelSftp.cd(workingDir);
        } catch (SftpException e) {
            e.printStackTrace();
        }
        currentDir = channelSftp.pwd();

    }

    @Override
    public void createFile(String path) throws IOException {
        try {
            channelSftp.put(path);
        } catch (SftpException e) {
            DialogProvider.showException(e);
        }
    }

    @Override
    public void createDirectory(String path) throws IOException {
        try {
            channelSftp.mkdir(path);
        } catch (SftpException e) {
            DialogProvider.showException(e);
        }
    }

    @Override
    public void rename(String s) throws IOException {
        try {
            channelSftp.rename(directoryView.getSelectedFile(), s);
        } catch (SftpException e) {
            DialogProvider.showException(e);
        }
    }

    @Override
    public FileInputStream getFileStream(String path) throws FileNotFoundException {
        return null;
    }

    @Override
    public void openFile(String file) {
        try {
            if (file.equals("..")) {
                channelSftp.cd("..");
                currentDir = channelSftp.pwd();
                directoryView.refresh();
                return;
            }
            try{
                channelSftp.cd(file);
                currentDir = channelSftp.pwd();
                directoryView.refresh();
                return;
            }catch (Exception ignored){}
            if (file != null) {
                try {
                    File temp = Files.createTempFile("",file).toFile();
                    channelSftp.get(file, new FileOutputStream(String.valueOf(temp)));
                    Desktop.getDesktop().open(temp);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    DialogProvider.showException(e);
                }
            }
        } catch (Exception e) {
            directoryView.refresh();
            DialogProvider.showException(e);
        }
    }

    @Override
    public List<String> delete(List<String> files) {
        for (String file: files) {
            try {
                channelSftp.rm(file);
            } catch (SftpException e) {
                DialogProvider.showException(e);
            }
        }
        return null;
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
    public void setView(DirectoryView view) {
        directoryView = view;
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
        List<String> filelist = new ArrayList<>();
        try {
            for (Object f:
                 channelSftp.ls(currentDir)) {
                filelist.add(((ChannelSftp.LsEntry)f).getFilename());
            }
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return filelist;
    }

    @Override
    public void saveFile(FileInputStream fis, String name) {
        try {
            channelSftp.put(fis, name);
        } catch (SftpException e) {
            DialogProvider.showException(e);
        }
    }
}
