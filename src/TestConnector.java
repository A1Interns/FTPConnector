/**
 * Created by bowenbaker on 6/12/14.
 */
public class TestConnector {

    public static void main(String[] args){
        FTPConnector ftp = new FTPConnector("ftp.filegenie.com", 21, "jasonzhang", "4705-PTGL");
        ftp.upload("/Users/bowenbaker/Desktop/README","README");
    }
}
