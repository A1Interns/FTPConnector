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

/**
 * Created by bowenbaker on 6/12/14.
 */
public class FTPConnector extends ClientModel {

    private FTPClient ftp;

    public FTPConnector(String host, int port, String user, String pwd){
        ftp = new FTPClient();
        hostname = host;
        port = port;
        username = user;
        password = pwd;
        initializeConnection();
        workingDirectory = "";
    }


    @Override
    public void initializeConnection() {
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
    public void changeWorkingDirectory(String newDirectory) {
        workingDirectory = newDirectory;
    }

    @Override
    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException f) {
                System.out.println("Failed to disconnect");
                f.printStackTrace();
            }
        }
    }

    @Override
    public void upload(String localFileFullName, String desiredDestinationFileName) {
        try{
            InputStream input = new FileInputStream(new File(localFileFullName));
            this.ftp.storeFile(workingDirectory + desiredDestinationFileName, input);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void download(String fileName, String localFilePath) {
        try {
            FileOutputStream fos = new FileOutputStream(localFilePath);
            this.ftp.retrieveFile(workingDirectory + fileName, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rename(String oldFileName, String newFileName) throws IOException {
        ftp.rename(oldFileName, newFileName);
    }

    @Override
    public String[] ls(String path, boolean includeFiles, boolean includeDirectories) throws IOException {
        ArrayList<String> list = new ArrayList<String>();
        if(includeFiles)
            for(FTPFile file : ftp.listFiles())
                list.add(file.getName());
        if(includeDirectories)
            for(FTPFile dir : ftp.listDirectories())
                list.add(dir.getName());
        return (String[]) list.toArray();
    }

    @Override
    public void mkdir(String directoryName) throws IOException {
        ftp.makeDirectory(workingDirectory + directoryName);
    }

    @Override
    public void rmdir(String directoryName) throws IOException {
        ftp.removeDirectory(workingDirectory + directoryName);
    }

    @Override
    public void rm(String fileName) throws IOException {
        ftp.deleteFile(workingDirectory + fileName);
    }
}
