import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by jasonzhang on 6/12/14.
 */
public class SFTPConnector extends ClientModel {
    private String host, user, pwd, currentDirectory, home;
    private int port = 22;
    private JSch jsch;
    private ChannelSftp channelSftp;
    private Session session;

    public SFTPConnector(String host, int port, String user, String pwd){


    }

    public SFTPConnector(String host, String user, String pwd){
        this.host = host;
        this.user = user;
        this.pwd = pwd;
        jsch = new JSch();
    }

    @Override
    public void changeWorkingDirectory(String newDirectory){
        currentDirectory = newDirectory;
        try {
            newDirectory = getAbsolutePath(newDirectory);
            channelSftp.cd(newDirectory);
        } catch (SftpException e){
            e.printStackTrace();
        }

    }

    public String getAbsolutePath(String path)
    {
        if (path.startsWith("/~"))
        {
            return home + path.substring(2, path.length());
        }

        // Already absolute!
        return path;
    }

    @Override
    public void disconnect() {
        channelSftp.disconnect();
        session.disconnect();
    }

    @Override
    public void upload(String localFileFullName, String desiredDestinationFileName)  {
        try {
            channelSftp.put(new FileInputStream(localFileFullName), desiredDestinationFileName);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (SftpException e){
            e.printStackTrace();
        }

    }

    @Override
    public void download(String fileName) {
        try {
            channelSftp.get(fileName);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    public void resumeDownload(String fileName, long skip){
        try {
            channelSftp.get(fileName, null, skip);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initilizeConnection() {
        
    }
}
