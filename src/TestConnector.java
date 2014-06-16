/**
 * Created by bowenbaker on 6/12/14.
 */
public class TestConnector {

    public static void main(String[] args){
        FTPSConnector ftps = new FTPSConnector("localhost",21,"bowenbaker","XXXXXX",100000,"P","SSL");
        System.out.println("trying ls");
        try{
            for(String file : ftps.ls("",true,true)) System.out.println(file);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
