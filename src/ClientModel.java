/**
 * Created by bowenbaker on 6/12/14.
 */
public abstract class ClientModel {

    protected boolean isConnected = false;
    protected String hostname, username, password, workingDirectory;
    protected int port;

    abstract public void disconnect();

    abstract public void upload(String localFileFullName, String desiredDestinationFileName);

    abstract public void download();

    abstract public boolean initilizeConnection();

    public void setHost(String newHost){hostname = newHost;}

    public String getHost() {return hostname;}

    public void setPort(int newPort){port = newPort;}

    public int getPort(){return port;}

    public void setUserName(String newName){username = newName;}

    public String getUserName(){return username;}

    public void setPassWord(String newPassWord){password = newPassWord;}

    public void setWorkingDirectory(String newDirectory){workingDirectory = newDirectory;}

    public String getWorkingDirectory(){return workingDirectory;}

}
