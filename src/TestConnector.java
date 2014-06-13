/**
 * Created by bowenbaker on 6/12/14.
 */
public class TestConnector {

    public static void main(String[] args){
        FTPSConnector ftps = new FTPSConnector("ftp.secureftp-test.com",990,"test","test",0);
        try{ftps.ls("",true,true);}
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
