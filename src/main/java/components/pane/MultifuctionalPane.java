package components.pane;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import components.directoryControl.DirectoryControl;
import components.directoryControl.local.DefaultLocalDirectoryControl;
import components.directoryControl.remote.FTPDirectoryControl;
import components.directoryControl.remote.SFTPDirectoryControl;
import components.directoryView.DirectoryView;
import components.directoryView.local.DefaultLocalDirectoryView;
import components.directoryView.remote.DefaultRemoteDirectoryView;
import javafx.scene.Node;
import javafx.scene.web.WebView;
import utils.DialogProvider;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class MultifuctionalPane implements Pane{
    Type type;
    DirectoryControl directoryControl;
    DirectoryView view;

    public MultifuctionalPane(String initialDir){
        setLocalDir(initialDir);
    }

    MultifuctionalPane(Type t) {
        this.setType(t);
    }

    private void setLocalDir(String initialDir){
        directoryControl = new DefaultLocalDirectoryControl(initialDir);
        DirectoryView view = new DefaultLocalDirectoryView(directoryControl);
        directoryControl.setView(view);
        this.view = view;
    }

    private void setFTPDir(String url){
        DirectoryView view = null;
        try {
            directoryControl = new FTPDirectoryControl(url);
            view = new DefaultRemoteDirectoryView(directoryControl);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        directoryControl.setView(view);
        this.view = view;
    }

    private void setSFTPDir(String url){
        DirectoryView view = null;
        try {
            directoryControl = new SFTPDirectoryControl(url);
            view = new DefaultRemoteDirectoryView(directoryControl);
        } catch (IOException | JSchException | URISyntaxException | SftpException e) {
            e.printStackTrace();
        }
        directoryControl.setView(view);
        this.view = view;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
        if (type != Type.LOCAL){
            String url = DialogProvider.showTextInputDialog("link", "link","link", "");
            if (type == Type.FTP){
                setFTPDir(url);
            } else if (type == Type.SSH){
                setSFTPDir(url);
            }
            return;
        }
        setLocalDir(File.listRoots()[0].getPath());
    }

    @Override
    public DirectoryControl getDirectoryController() {
        return directoryControl;
    }

    @Override
    public DirectoryView getView() {
        return view;
    }
}
