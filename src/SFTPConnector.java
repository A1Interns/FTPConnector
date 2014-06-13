import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private Log logger = LogFactory.getLog(getClass());

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
    public void rename(String oldFileName, String newFileName) {
        try {
            channelSftp.rename(oldFileName, getAbsolutePath(newFileName));
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] ls(String path, boolean includeFiles, boolean includeDirectories) throws IOException {
        return null;
    }

    @Override
    public void mkdir(String directoryName) {
        try {
            channelSftp.mkdir(getAbsolutePath(directoryName));
        } catch (SftpException e){
            e.printStackTrace();
        }
    }

    @Override
    public void rmdir(String directoryName) {
        try {
            channelSftp.rmdir(directoryName);
        } catch (SftpException e){
            e.printStackTrace();
        }
    }

    @Override
    public void rm(String fileName) {
        try {
            channelSftp.rm(fileName);
        } catch (SftpException e){
            e.printStackTrace();
        }
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
            logger.error("File to be uploaded was not found");
            e.printStackTrace();
        } catch (SftpException e){
            logger.error("Error during uploading");
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
