/**
 * Created by bowenbaker on 6/12/14.
 */
public interface ClientModel {

    public void changeWorkingDirectory(String newDirectory);

    public void disconnect();

    public void upload(String localFileFullName, String desiredDestinationFileName);

    public void download();

}
