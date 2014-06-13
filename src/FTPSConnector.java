import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by jasonzhang on 6/12/14.
 */

import org.apache.commons.net.ftp.FTPSClient;


public class FTPSConnector extends ClientModel {

    private FTPSClient ftpsClient;

    public FTPSConnector(String hostname, String username, String pwd){
        this.hostname = hostname;
        this.username = username;
        this.password = pwd;
    }


    private FTPSClient ftps;

    public FTPSConnector(String host, int port, String user, String pwd, String protocol){
        setParams(host, port, user, pwd);
        ftps = new FTPSClient(protocol);
        isConnected = initilizeConnection();
    }

    public FTPSConnector(String host, int port, String user, String pwd){
        setParams(host, port, user, pwd);
        ftps = new FTPSClient();
        isConnected = initilizeConnection();
    }

    private void setParams(String host, int port, String user, String pwd){
        hostname = host;
        port = port;
        username = user;
        password = pwd;
    }

    @Override
    public void rename(String oldFileName, String newFileName) throws IOException {

    }

    @Override
    public String[] ls(String path, boolean includeFiles, boolean includeDirectories) throws IOException {
        return new String[0];
    }

    @Override
    public void mkdir(String directoryName) throws IOException {

    }

    @Override
    public void rmdir(String directoryName) throws IOException {

    }

    @Override
    public void rm(String fileName) throws IOException {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void upload(String localFileFullName, String desiredDestinationFileName) {

    }

    @Override
    public void download(String fileName) {

    }

    @Override
    public boolean initilizeConnection() {
        ftps.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        try {
            ftps.connect(hostname, port);
            reply = ftps.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftps.disconnect();
            }
            ftps.login(username, password);
            ftps.setFileType(FTP.BINARY_FILE_TYPE); //TODO different file types?
            ftps.enterLocalPassiveMode();
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    @Override
    public void changeWorkingDirectory(String newDirectory) {

    }
}
