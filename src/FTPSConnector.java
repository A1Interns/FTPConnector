import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * FTPS Class:
 *  supports both explicit and implicit FTPS
 */

import org.apache.commons.net.ftp.FTPSClient;
import java.util.ArrayList;


public class FTPSConnector extends ClientModel {

    private FTPSClient ftps;
    private String prot = "P";
    private String protocol = "SSL";
    private long bufSize = 0;
    private boolean isExplicit;

    /**
     * This constructor is for implicit FTPS and you don't have to specify a protocol.
     * @param host hostname
     * @param user username
     * @param pwd password
     * @param bufferSize 0 standard
     */
    public FTPSConnector(String host, int port, String user, String pwd,long bufferSize){
        isExplicit = false;
        ftps = new FTPSClient();
        setParams(host, port, user, pwd);
        try{initializeConnection();}
        catch(IOException e){
            System.out.println("Unable to initialize");
            e.printStackTrace();
        }
        if(isConnected) {
            try {
                ftps.execPBSZ(bufferSize);
            }
            catch(Exception e){
                System.out.println("Failed to initilize FTPES parameters");
            }
        }
    }

    /**
     *This constructor is for explicit FTPS, and you must specify a protocol
     *
     * @param host hostname
     * @param port port number : 21 for explicit, 990 for implicit
     * @param user username
     * @param pwd password
     * @param protocol "SSL" or "TLS"
     * @param bufferSize 0 is standard
     * @param prot protection, "P" is standard but :
     *             "C" : Clear
     *             "S" : Safe (SSL Protocol only)
     *             "E" : Confidential (SSL Protocol only)
     *             "P" : Private
     */
    public FTPSConnector(String host, int port, String user, String pwd, long bufferSize, String prot, String protocol){
        isExplicit = true;
        setParams(host, port, user, pwd);
        ftps = new FTPSClient(protocol);
        this.prot = prot;
        this.protocol = protocol;
        this.bufSize = bufferSize;
        try{initializeConnection();}
        catch(IOException e){
            System.out.println("Unable to initialize");
        }
    }

    private void setParams(String host, int port, String user, String pwd){
        hostname = host;
        this.port = port;
        username = user;
        password = pwd;
    }

    @Override
    public void rename(String oldFileName, String newFileName) throws IOException {
        ftps.rename(oldFileName,newFileName);
    }

    @Override
    public String[] ls(String path, boolean includeFiles, boolean includeDirectories) throws IOException {
        ArrayList<String> list = new ArrayList<String>();
        if(includeFiles)
            for(FTPFile file : ftps.listFiles())
                list.add(file.getName());
        if(includeDirectories)
            for(FTPFile dir : ftps.listDirectories())
                list.add(dir.getName());
        return list.toArray(new String[list.size()]);
    }

    @Override
    public void mkdir(String directoryName) throws IOException {
        ftps.makeDirectory(workingDirectory + directoryName);
    }

    @Override
    public void rmdir(String directoryName) throws IOException {
        ftps.removeDirectory(workingDirectory + directoryName);
    }

    @Override
    public void rm(String fileName) throws IOException {
        ftps.deleteFile(workingDirectory + fileName);
    }

    @Override
    public void disconnect() {
        if (this.ftps.isConnected()) {
            try {
                this.ftps.logout();
                this.ftps.disconnect();
                isConnected = false;
            } catch (IOException f) {
                System.out.println("Failed to disconnect");
                f.printStackTrace();
            }
        }
    }

    @Override
    public void upload(String localFileFullName, String desiredDestinationFileName) {
        try{
            InputStream input = new FileInputStream(new File(localFileFullName));
            this.ftps.storeFile(workingDirectory + desiredDestinationFileName, input);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void download(String fileName, String localFilePath) {
        try {
            FileOutputStream fos = new FileOutputStream(localFilePath);
            this.ftps.retrieveFile(workingDirectory + fileName, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initializeConnection() throws IOException{
        ftps.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        System.out.println("added prot listener");
        try{
            ftps.connect(hostname, port);
        }catch(Exception e){}
        System.out.println("connected");
        ftps.login(username,password);
        System.out.println("logged in");
        if(isExplicit) {
            ftps.execAUTH(protocol);
            ftps.execPBSZ(bufSize);
            ftps.execPROT(prot);
        } else ftps.execPBSZ(bufSize);
        reply = ftps.getReplyCode();
        System.out.println("reply is : " + reply);
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftps.disconnect();
            return;
        }
        isConnected = true;
        ftps.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftps.login(username, password);
        ftps.setFileType(FTP.BINARY_FILE_TYPE); //TODO different file types?
        ftps.enterLocalPassiveMode();
    }


    /**
     * Change directory inside FTP Server
     * @param newDirectory must end in "/"
     */
    @Override
    public void changeWorkingDirectory(String newDirectory) {
        workingDirectory = newDirectory;

    }

    @Override
    public String[] ls(String path) throws IOException {
        return new String[0];
    }

    @Override
    public String[] ls() throws IOException {
        return new String[0];
    }
}
