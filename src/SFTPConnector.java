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
import java.util.Properties;


/**
 * Created by jasonzhang on 6/12/14.
 */
public class SFTPConnector extends ClientModel {
    public static final String CHANNEL_SFTP = "sftp";
    public static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
    private int port;
    private JSch jsch;
    private ChannelSftp channelSftp;
    private Session session;

    public SFTPConnector(String hostname, String user, String pwd){
        this(hostname, 22, user, pwd);
    }

    public SFTPConnector(String hostname, int port, String user, String pwd){
        this.hostname = hostname;
        this.port = port;
        this.username = user;
        this.password = pwd;
    }

    public SFTPConnector(String host){
        this.hostname = host;
        jsch = new JSch();
    }

    @Override
    public void changeWorkingDirectory(String newDirectory){
        workingDirectory = newDirectory;
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
        if (channelSftp != null) {
            channelSftp.disconnect();
        }
        if (session != null){
            session.disconnect();
        }
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



    public boolean initilizeConnection() {
        boolean connected = false;
        try {
            Properties hash = new Properties();
            hash.put(STRICT_HOST_KEY_CHECKING, "no");
            session = jsch.getSession(username, hostname);
            session.setConfig(hash);
            session.setPort(port);
            session.setPassword(password);
            session.connect();
            Channel channel = session.openChannel(CHANNEL_SFTP);
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            this.home = channelSftp.pwd();
            return true;
        } catch (JSchException e){
            e.printStackTrace();
            return false;
        } catch (SftpException e){
            e.printStackTrace();
            return false;
        }
    }
}
