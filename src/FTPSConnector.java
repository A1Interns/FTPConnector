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

        return false;
    }

    @Override
    public void changeWorkingDirectory(String newDirectory) {

    }
}
