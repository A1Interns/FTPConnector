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
public class FTPConnector implements ClientModel {

    private String hostname, username, password, workingDirectory;
    private FTPClient ftp;

    public FTPConnector(String host, int port, String user, String pwd){
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        try {
            ftp.connect(host, port);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
            }
            ftp.login(user, pwd);
            ftp.setFileType(FTP.BINARY_FILE_TYPE); //TODO different file types?
            ftp.enterLocalPassiveMode();
        }
        catch(Exception e){
            System.out.println("unable to connect");
        }
        workingDirectory = "";
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
    public void download() {

    }
}
