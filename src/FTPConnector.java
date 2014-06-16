import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by bowenbaker on 6/12/14.
 */
public class FTPConnector extends ClientModel {

    private FTPClient ftp;

    /**
     *
     * @param host hostname
     * @param port port : standard is 21
     * @param user username
     * @param pwd password
     */
    public FTPConnector(String host, int port, String user, String pwd){
        ftp = new FTPClient();
        hostname = host;
        this.port = port;
        username = user;
        password = pwd;
        initializeConnection();
        workingDirectory = "";
    }


    @Override
    public boolean initializeConnection() {
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        try {
            ftp.connect(hostname, port);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
            }
            ftp.login(username, password);
            ftp.setFileType(FTP.BINARY_FILE_TYPE); //TODO different file types?
            ftp.enterLocalPassiveMode();
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    /**
     * Change directory inside FTP Server
     * @param newDirectory must end in "/"
     */
    @Override
    public boolean cd(String newDirectory) {
        workingDirectory = newDirectory;
        return true;
    }

    @Override
    public boolean disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
                return true;
            } catch (IOException f) {
                //TODO Logger
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean upload(String localFileFullName, String desiredDestinationFileName) {
        try{
            InputStream input = new FileInputStream(new File(localFileFullName));
            this.ftp.storeFile(workingDirectory + desiredDestinationFileName, input);
            return true;
        }
        catch(Exception e){
            //TODO Logger
            return false;
        }
    }

    @Override
    public boolean download(String fileName, String localFilePath) {
        try {
            FileOutputStream fos = new FileOutputStream(localFilePath);
            this.ftp.retrieveFile(workingDirectory + fileName, fos);
            return true;
        } catch (IOException e) {
            //TODO Logger
            return false;
        }
    }

    @Override
    public boolean rename(String oldFileName, String newFileName){
        try {
            ftp.rename(oldFileName, newFileName);
            return true;
        } catch(Exception e){
            //TODO Logger
            return false;
        }
    }

    @Override
    public Vector<String> ls(String path, boolean includeFiles, boolean includeDirectories){
        Vector<String> list = new Vector<String>();
        try {
            if (includeFiles)
                for (FTPFile file : ftp.listFiles())
                    list.add(file.getName());
            if (includeDirectories)
                for (FTPFile dir : ftp.listDirectories())
                    list.add(dir.getName());
        } catch(Exception e){
            //TODO Logger
        }
        return list;
    }

    @Override
    public boolean mkdir(String directoryName){
        try {
            ftp.makeDirectory(workingDirectory + directoryName);
            return true;
        } catch(Exception e){
            //TODO Logger
            return false;
        }
    }

    @Override
    public boolean rmdir(String directoryName){
        try {
            ftp.removeDirectory(workingDirectory + directoryName);
            return true;
        } catch(Exception e){
            //TODO Logger
            return false;
        }
    }

    @Override
    public boolean rm(String fileName){
        try {
            ftp.deleteFile(workingDirectory + fileName);
            return true;
        } catch(Exception e){
            //TODO Logger
            return false;
        }
    }


}
