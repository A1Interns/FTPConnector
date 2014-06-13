import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;


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

    public SFTPConnector(String hostname, String user, String pwd) {
        this(hostname, 2222, user, pwd);
    }

    public SFTPConnector(String hostname, int port, String user, String pwd) {
        this.hostname = hostname;
        this.port = port;
        this.username = user;
        this.password = pwd;
        jsch = new JSch();
    }

    @Override
    public boolean cd(String newDirectory) {
        workingDirectory = newDirectory;
        try {
            newDirectory = getAbsolutePath(newDirectory);
            channelSftp.cd(newDirectory);
            return true;
        } catch (SftpException e) {
            logger.severe("Error duing changing working directory from " + getAbsolutePath(workingDirectory) + " to " + newDirectory);
            return false;
        }

    }

    public String getAbsolutePath(String path) {
        if (path.startsWith("/~")) {
            return home + path.substring(2, path.length());
        }
        // Already absolute!
        return path;
    }

    @Override
    public boolean rename(String oldFileName, String newFileName) {
        try {
            channelSftp.rename(oldFileName, newFileName);
        } catch (SftpException e) {
            logger.severe("Error during renaming file " + oldFileName + " to " + newFileName);
            return false;
        }
        return true;
    }

    @Override
    public Vector<String> ls(String path, boolean includeFiles, boolean includeDirectories) {
        Vector<String> entriesNames = new Vector<String>();
        try {
            Vector<LsEntry> lsEntries = channelSftp.ls(path);
            for (LsEntry entry : lsEntries) {
                if (!entry.getAttrs().isDir() && includeFiles) {
                    entriesNames.add(entry.getFilename());
                } else if (entry.getAttrs().isDir() && includeDirectories) {
                    entriesNames.add(entry.getFilename());
                }
            }
        } catch (SftpException e) {
            logger.severe("Error during listing directories in " + path);
        }
        return entriesNames;
    }

    @Override
    public boolean mkdir(String directoryName) {
        try {
            channelSftp.mkdir(getAbsolutePath(directoryName));
        } catch (SftpException e) {
            logger.severe("Error during creating directory" + directoryName);
            return true;
        }
        return false;
    }

    @Override
    public boolean rmdir(String directoryName) {
        try {
            channelSftp.rmdir(directoryName);
        } catch (SftpException e) {
            logger.severe("Error during removing directory" + directoryName);
            return false;
        }
        return true;
    }

    @Override
    public boolean rm(String fileName) {
        try {
            channelSftp.rm(fileName);
        } catch (SftpException e) {
            logger.severe("Error during removing file " + fileName);
            return false;
        }
        return true;
    }

    @Override
    public boolean disconnect() {
        if (channelSftp != null) {
            channelSftp.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
        return true;
    }

    @Override
    public boolean upload(String localFileFullName, String desiredDestinationFileName) {
        try {
            channelSftp.put(new FileInputStream(localFileFullName), desiredDestinationFileName);
        } catch (FileNotFoundException e) {
            logger.severe("File to be uploaded was not found");
            return false;
        } catch (SftpException e) {
            logger.severe("Error during uploading");
            return false;
        }
        return true;
    }

    @Override
    public boolean download(String fileName, String localFilePath) {
        try {
            channelSftp.get(fileName);
        } catch (SftpException e) {
            logger.severe("Error during downloading");
            return false;
        }
        return true;
    }

    public boolean resumeDownload(String fileName, long skip) throws IOException {
        try {
            channelSftp.get(fileName, null, skip);
        } catch (SftpException e) {
            logger.severe("Error during resuming download");
            return false;
        }
        return true;
    }

    @Override
    public boolean initializeConnection() {
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
            this.workingDirectory = home;
        } catch (JSchException e) {
            logger.severe("JSchException: " + e.getMessage());
            return false;
        } catch (SftpException e) {
            logger.severe("SftpException: " + e.getMessage());
            return false;
        }
        return true;
    }


    private boolean fileExists(String fileName) {
        Vector<String> fileNames = ls(".", true, true);
        for (String file : fileNames) {
            if (file.equals(fileName)) {
                return true;
            }
        }
        return false;
    }
}
