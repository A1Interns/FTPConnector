import java.io.IOError;
import java.io.IOException;

/**
 * Created by bowenbaker on 6/12/14.
 */
public abstract class ClientModel {

    protected boolean isConnected = false;
    protected String hostname, username, password, workingDirectory, home;
    protected int port;

    abstract public void rename(String oldFileName, String newFileName) throws IOException;

    abstract public String[] ls() throws IOException;

    abstract public String[] ls(String path) throws IOException;

    abstract public String[] ls(String path, boolean includeFiles, boolean includeDirectories) throws IOException;

    abstract public void mkdir(String directoryName) throws IOException;

    abstract public void rmdir(String directoryName) throws IOException;

    abstract public void rm(String fileName) throws IOException;

    abstract public void disconnect();

    abstract public void upload(String localFileFullName, String desiredDestinationFileName) throws IOException;

    abstract public void download(String fileName, String localFilePath) throws IOException;

    abstract public void initializeConnection() throws IOException;

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

    public abstract void changeWorkingDirectory(String newDirectory) throws IOException;

    public String getWorkingDirectory() {
        return workingDirectory;
    }

}
