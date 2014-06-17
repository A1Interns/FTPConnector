import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.Before;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * NOTE ::: not complete, part way through mocking all of the apache FTPClient methods
 * used in the FTPConnector.
 *
 * Created by bowenbaker on 6/16/14.
 */
public class UnitTests {


    //For FTPConnector
    String pathToTestFTPFolder = "/Users/bowenbaker/Desktop/FTPServer/";
    String testHost = "host";
    String testUser = "user";
    String testPW = "pw";
    int testPort = 0;
    FTPClient ftpClient =  mock(FTPClient.class);
    FTPConnector FTPcon = new FTPConnector(testHost,testPort,testUser,testPW);

    @Before
    public void setup() throws Exception{
        doNothing().when(ftpClient).connect(testHost,testPort);
        doNothing().when(ftpClient).disconnect();
        int fakeReply = 1;
        when(ftpClient.getReplyCode()).thenReturn(fakeReply);
        when(FTPReply.isPositiveCompletion(fakeReply)).thenReturn(true);
        doNothing().when(ftpClient).login(testUser, testPW);
        doNothing().when(ftpClient).setFileType(FTP.BINARY_FILE_TYPE);
        doNothing().when(ftpClient).enterLocalPassiveMode();
        doNothing().when(ftpClient).logout();
        when(ftpClient.storeFile(anyString(),(InputStream) anyObject())).thenAnswer(new Answer() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                inputStreamToFile((InputStream) args[1], (String) args[0]);
                return "done";
            }
        });
    }

    @Test
    public void testInputStreamToFile(){
        try {
            InputStream in = new FileInputStream(pathToTestFTPFolder + "test.rtf");
            inputStreamToFile(in, pathToTestFTPFolder + "test1.rtf");
            File isThere = new File(pathToTestFTPFolder + "test1.rtf");
            assert(isThere.exists());
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    public void inputStreamToFile(InputStream in, String fileName){
        OutputStream out = null;
        try {
            // write the inputStream to a FileOutputStream
            out = new FileOutputStream(new File(pathToTestFTPFolder + fileName));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    // outputStream.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }






}
