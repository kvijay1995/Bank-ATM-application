import java.rmi.Remote;
import java.rmi.RemoteException;


public interface TellerInterface extends Remote
{
String openNewAccount(String  accountType,
                      String  customerName) throws RemoteException;

String processAccount(String  processType,
                      Integer accountNumber,
                      Double  amount)       throws RemoteException;

String showAccount   (Integer accountNumber)throws RemoteException;

String showAccounts  (String  customerName) throws RemoteException;
}
