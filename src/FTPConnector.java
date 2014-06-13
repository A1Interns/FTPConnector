import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

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
        isConnected = initilizeConnection();
        workingDirectory = "";
    }


    @Override
    public boolean initilizeConnection() {
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

    @Override
    public void changeWorkingDirectory(String newDirectory) {
        workingDirectory = newDirectory;
    }

    @Override
    public void disconnect() {

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
    public void download(String fileName) {

    }
}
