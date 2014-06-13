import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Created by bowenbaker on 6/12/14.
 */
public abstract class ClientModel {

    final static Logger logger = Logger.getLogger(ClientModel.class.getName());
    protected boolean isConnected = false;
    protected String hostname, username, password, workingDirectory, home;
    protected int port;

    abstract public boolean rename(String oldFileName, String newFileName);

    abstract public Vector<String> ls();

    abstract public Vector<String> ls(String path);

    abstract public Vector<String> ls(String path, boolean includeFiles, boolean includeDirectories);

    abstract public boolean mkdir(String directoryName);

    abstract public boolean rmdir(String directoryName);

    abstract public boolean rm(String fileName);

    abstract public boolean disconnect();

    abstract public boolean upload(String localFileFullName, String desiredDestinationFileName);

    abstract public boolean download(String fileName, String localFilePath);

    abstract public boolean initializeConnection();

    public void setHost(String newHost) {
        hostname = newHost;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getHost() {
        return hostname;
    }

    public void setPort(int newPort) {
        port = newPort;
    }

    public int getPort() {
        return port;
    }

    public void setUserName(String newName) {
        username = newName;
    }

    public String getUserName() {
        return username;
    }

    public void setPassWord(String newPassWord) {
        password = newPassWord;
    }

    public abstract boolean cd(String newDirectory);

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String newDir) {workingDirectory = newDir;}

    public boolean[] uploadFiles(String[] files){
        boolean[] outcomes = new boolean[files.length];
        for (int i = 0; i < files.length; i++){
            outcomes[i] = upload(files[i], files[i]);
        }
        return outcomes;
    }

    public boolean[] downloadFiles(String[] files){
        boolean[] outcomes = new boolean[files.length];
        for (int i = 0; i < files.length; i++){
            outcomes[i] = download(files[i], files[i]);
        }
        return outcomes;
    }

}
