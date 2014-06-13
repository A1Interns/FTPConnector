import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    public SFTPConnector(String hostname, String user, String pwd){
        this(hostname, 2222, user, pwd);
    }

    public SFTPConnector(String hostname, int port, String user, String pwd){
        this.hostname = hostname;
        this.port = port;
        this.username = user;
        this.password = pwd;
        jsch = new JSch();
    }


    @Override
    public void changeWorkingDirectory(String newDirectory) throws IOException{
        workingDirectory = newDirectory;
        try {
            newDirectory = getAbsolutePath(newDirectory);
            channelSftp.cd(newDirectory);
        } catch (SftpException e){
            logger.error("Error duing changing working directory from " + getAbsolutePath(workingDirectory) + " to " + newDirectory);
            throw new IOException(e.getMessage());
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
    public void rename(String oldFileName, String newFileName) throws IOException{
        try {
            channelSftp.rename(oldFileName, newFileName);
        } catch (SftpException e) {
            logger.error("Error during renaming file " + oldFileName + " to " + newFileName);
            throw new IOException(e.getMessage());
        }
    }

    public String[] ls(String path) throws IOException{
        return ls(path, true, true);
    }

    public String[] ls() throws IOException{
        return ls(".", true, true);
    }

    @Override
    public String[] ls(String path, boolean includeFiles, boolean includeDirectories) throws IOException {
        Vector<String> entriesNames = new Vector<String>();
        try {
            Vector<LsEntry> lsEntries = channelSftp.ls(path);
            for (LsEntry entry : lsEntries){
                if (!entry.getAttrs().isDir() && includeFiles){
                    entriesNames.add(entry.getFilename());
                }
                else if (entry.getAttrs().isDir() && includeDirectories){
                    entriesNames.add(entry.getFilename());
                }
            }
        } catch (SftpException e){
            logger.error("Error during listing directories in " + path);
            throw new IOException(e.getMessage());
        }
        return entriesNames.toArray(new String[entriesNames.size()]);
    }

    @Override
    public void mkdir(String directoryName) throws IOException{
        try {
            channelSftp.mkdir(getAbsolutePath(directoryName));
        } catch (SftpException e){
            logger.error("Error during creating directory" + directoryName);
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void rmdir(String directoryName) throws IOException{
        try {
            channelSftp.rmdir(directoryName);
        } catch (SftpException e){
            logger.error("Error during removing directory" + directoryName);
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void rm(String fileName) throws IOException{
        try {
            channelSftp.rm(fileName);
        } catch (SftpException e){
            logger.error("Error during removing file " + fileName);
            throw new IOException(e.getMessage());
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
    public void upload(String localFileFullName, String desiredDestinationFileName) throws IOException {
        try {
            channelSftp.put(new FileInputStream(localFileFullName), desiredDestinationFileName);
        } catch (FileNotFoundException e){
            logger.error("File to be uploaded was not found");
            throw new IOException(e.getMessage());
        } catch (SftpException e){
            logger.error("Error during uploading");
            throw new IOException(e.getMessage());
        }

    }

    @Override
    public void download(String fileName, String localFilePath) throws IOException {
        try {
            channelSftp.get(fileName);
        } catch (SftpException e) {
            logger.error("Error during downloading");
            throw new IOException(e.getMessage());
        }
    }

    public void resumeDownload(String fileName, long skip) throws IOException{
        try {
            channelSftp.get(fileName, null, skip);
        } catch (SftpException e) {
            logger.error("Error during resuming download");
            throw new IOException(e.getMessage());
        }
    }



    public void initializeConnection() throws IOException{
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
        } catch (JSchException e){
            logger.error("JSchException: " + e.getMessage());
            throw new IOException(e.getMessage());
        } catch (SftpException e){
            logger.error("SftpException: " + e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

    private static void printStringArray(String[] array){
        for (String str : array) System.out.println(str);
    }

    private boolean fileExists(String fileName){
        try {
            String[] fileNames = ls();
            for (String file : fileNames){
                if (file.equals(fileName)){
                    return true;
                }
            }
            return false;

        } catch (IOException e){

        }
        return false;
    }

    public static void main(String[] args){
        SFTPConnector sftpConnector = new SFTPConnector("sftp.agilone.com", "jason.zhang", "Agil1234");
        try {
            sftpConnector.initializeConnection();
            System.out.println("Connected");
            sftpConnector.changeWorkingDirectory("dir1");
            sftpConnector.upload("src/ClientModel.java", "ClientModel.java");
            sftpConnector.changeWorkingDirectory("..");
            printStringArray(sftpConnector.ls());
        } catch (IOException e){
            System.out.println("failed");
        }
    }
}
