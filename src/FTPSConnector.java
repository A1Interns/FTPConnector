import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.commons.net.ftp.FTPSClient;
import java.util.Vector;

/**
 * FTPS Class:
 *  supports both explicit and implicit FTPS
 */
public class FTPSConnector extends ClientModel {

   // Logger logger = Logger.getLogger();

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
        try{isConnected = initializeConnection();}
        catch(Exception e){
            logger.severe("Unable to initialize implicit FTPS. Host : " + host + ", port : " + port + ", user : " + user);
        }
        if(isConnected) {
            try {
                ftps.execPBSZ(bufferSize);
            }
            catch(Exception e){
                logger.severe("Failed to initilize FTPES parameters");
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
        catch(Exception e){
            logger.severe("Unable to initialize explicit FTPS. Host : " + host + ", port : " + port + ", user : " + user);
        }
    }

    private void setParams(String host, int port, String user, String pwd){
        hostname = host;
        this.port = port;
        username = user;
        password = pwd;
    }

    @Override
    public boolean rename(String oldFileName, String newFileName){
        try {
            ftps.rename(oldFileName, newFileName);
            return true;
        } catch(Exception e){
            logger.severe("Failed to rename : " + oldFileName + " to " + newFileName);
            return false;
        }

    }

    @Override
    public Vector<String> ls(String path, boolean includeFiles, boolean includeDirectories){
        Vector<String> list = new Vector<String>();
        try {
            if (includeFiles)
                for (FTPFile file : ftps.listFiles())
                    list.add(file.getName());
            if (includeDirectories)
                for (FTPFile dir : ftps.listDirectories())
                    list.add(dir.getName());
        } catch(Exception e){
            logger.severe("Invalid ls path : " + path);
        }
        return list;
    }

    @Override
    public boolean mkdir(String directoryName){
        try {
            ftps.makeDirectory(workingDirectory + directoryName);
            return true;
        }catch(Exception e){
            logger.severe("failed to make directory with name : " + directoryName);
            return false;
        }
    }

    @Override
    public boolean rmdir(String directoryName){
        try {
            ftps.removeDirectory(workingDirectory + directoryName);
            return true;
        }catch(Exception e){
            logger.severe("failed to remove directory with name : " + workingDirectory + directoryName);
            return false;
        }
    }

    @Override
    public boolean rm(String fileName){
        try {
            ftps.deleteFile(workingDirectory + fileName);
            return false;
        }catch(Exception e){
            logger.severe("failed to remove file with name : " + workingDirectory + fileName);
            return false;
        }
    }

    @Override
    public boolean disconnect() {
        if (this.ftps.isConnected()) {
            try {
                this.ftps.logout();
                this.ftps.disconnect();
                isConnected = false;
                return true;
            } catch (IOException f) {
                logger.severe("failed to disconnect");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean upload(String localFileFullName, String desiredDestinationFileName) {
        try{
            InputStream input = new FileInputStream(new File(localFileFullName));
            this.ftps.storeFile(workingDirectory + desiredDestinationFileName, input);
            return true;
        }
        catch(Exception e){
            logger.severe("failed to upload file : " + localFileFullName + " to folder : " + workingDirectory + desiredDestinationFileName);
            return false;
        }
    }

    @Override
    public boolean download(String fileName, String localFilePath) {
        try {
            FileOutputStream fos = new FileOutputStream(localFilePath);
            this.ftps.retrieveFile(workingDirectory + fileName, fos);
            return true;
        } catch (IOException e) {
            logger.severe("failed to download : " + workingDirectory + fileName + " to : " + localFilePath);
            return false;
        }
    }

    @Override
    public boolean initializeConnection(){
        ftps.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        try{
            ftps.connect(hostname, port);
        }catch(Exception e){
            logger.severe("failed to obtain socket connection at host : " + hostname +  ", port : " + port);
        }

        try {
            ftps.login(username, password);
            if (isExplicit) {
                ftps.execAUTH(protocol);
                ftps.execPBSZ(bufSize);
                ftps.execPROT(prot);
            } else ftps.execPBSZ(bufSize);
            reply = ftps.getReplyCode();
            System.out.println("reply is : " + reply);
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.severe("received negative reply from server");
                ftps.disconnect();
                return false;
            }
            ftps.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
            ftps.login(username, password);
            ftps.setFileType(FTP.BINARY_FILE_TYPE); //TODO different file types?
            ftps.enterLocalPassiveMode();
            return true;
        } catch(Exception e){
            return false;
        }
    }

    @Override
    public boolean cd(String newDirectory){
        workingDirectory = newDirectory;
        return true;
    }
}
