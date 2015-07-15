import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FinalAnswerInterface extends Remote 
{
	public String getAnswer(String studentName, String studentPassword) throws RemoteException;
}
