import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.sql.*;

public class BankServerDB extends UnicastRemoteObject implements TellerInterface
{
	private ConcurrentHashMap<Integer, CashAccount> accounts = new ConcurrentHashMap<Integer, CashAccount>();
	private Connection connection;
	private PreparedStatement insertStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement deleteStatement;
	private Statement selectAllStatement;

	public static void main(String[] args) throws Exception
	{
		new BankServerDB();
	}

	public BankServerDB() throws Exception //CONSTRUCTOR
	{
		super();
		Naming.rebind("TellerServer", this);//register myself with
											// the rmiregistry
		System.out.println("TellerServer is up at "
			           + InetAddress.getLocalHost().getHostAddress() );
		
		// load the database driver and make connections.
		Class.forName("com.ibm.db2j.jdbc.DB2jDriver");
        System.out.println("Driver loaded!");
        connection = DriverManager.getConnection(
                          "jdbc:db2j:C:\\Users\\Vijay\\Downloads\\database\\QuoteDB");
        System.out.println("Connection made to Data Base!");
        
        insertStatement = connection.prepareStatement(
        	     "INSERT INTO BANK_ACCOUNTS "
        	   + "(ACCOUNT_NUMBER, ACCOUNT_TYPE, CUSTOMER_NAME, BALANCE) "
        	   + "VALUES (?,?,?,?)");
		
        updateStatement = connection.prepareStatement(
                "UPDATE BANK_ACCOUNTS "
              + "SET BALANCE = ? "
              + "WHERE ACCOUNT_NUMBER = ?");
        
        deleteStatement = connection.prepareStatement(
                "DELETE FROM BANK_ACCOUNTS "
              + "WHERE ACCOUNT_NUMBER = ?");
        
        selectAllStatement = connection.createStatement();
        ResultSet rs = selectAllStatement.executeQuery(
                "SELECT * FROM BANK_ACCOUNTS");
        
        while (rs.next()) // read the next row of the ResultSet
        {
		    // get the column values for this row
		    int    accountNumber = rs.getInt   ("ACCOUNT_NUMBER");
		    String accountType   = rs.getString("ACCOUNT_TYPE");
		    String customerName  = rs.getString("CUSTOMER_NAME");
		    double balance       = rs.getDouble("BALANCE");
		    System.out.println(" acct#="    + accountNumber
		            + " acctType=" + accountType
		            + " custName=" + customerName
		            + " balance="  + balance);
		    
		    CashAccount ca;
		    if (accountType.equalsIgnoreCase(Bank.CHECKING))
		           ca = CheckingAccount.restoreFromDataBase(customerName, balance, accountNumber);
		    else if (accountType.equalsIgnoreCase(Bank.SAVINGS))
		           ca = SavingsAccount.restoreFromDataBase(customerName, balance, accountNumber);
		    else    { 
		       System.out.println("SYSTEM ERROR: account type " + accountType + " is not recognized when reading DB table BANK_ACCOUNTS in server constructor.");
		       continue; // skip unrecognized account
		       }
		    
		    accounts.put(accountNumber, ca);
		    
		    System.out.println(accounts); // EASY!
        }

	}

	public String openNewAccount(String accountType, String customerName)
	{
		CashAccount ca = null;
		try
		{
			if (accountType.equals(Bank.CHECKING))
		         ca = new CheckingAccount(customerName);
			else if (accountType.equals(Bank.SAVINGS))
		         ca = new SavingsAccount(customerName);
			else return "ERROR: account type " + accountType
		          + " is not recognized by the server."
		          + " Call the IT department!";
		}
		catch(Exception e)
		{	}
		
		// Add the new account to the collection.
		accounts.put(ca.getAccountNumber(), ca);
		
		// Add a new row to the DB table for this new account
		try {
		    insertStatement.setInt   (1, ca.getAccountNumber());
		    insertStatement.setString(2, accountType);
		    insertStatement.setString(3, customerName);
		    insertStatement.setDouble(4, 0); // initial balance for a new account
		    insertStatement.executeUpdate();
		    }
		catch(SQLException sqle)
		    {
		    return "ERROR: Unable to add new account to the data base."
		          + sqle.toString();
		    }
		
		/*try
		{
			//saveAccounts();
		}
		catch(IOException ioe)
		{
			return ioe.getMessage();
		}*/
	
		return ca.toString();
	}	

	
	public String showAccount(Integer accountNumber)
	{
		CashAccount ca = accounts.get(accountNumber);
		if(ca == null)
			return "Account not found!";
		else
			return ca.toString();
	}

	public String showAccounts(String customerName) 
	{
		CashAccount[] accountList = accounts.values().toArray(new CashAccount[0]);
		TreeSet<String> hitList = new TreeSet<String>();
		for(CashAccount ca : accountList)
		{
			if(ca.getCustomerName().toUpperCase().startsWith(customerName.toUpperCase()))
				hitList.add(ca.getCustomerName() + " " + ca.toString());
		}
		if(hitList.isEmpty())
			return "No accounts starting with the entered name was found";
		else
		{
			String hitString = "";
			for(String name : hitList)
				hitString += name + "\n";
			return hitString;
		}
	}

	
	public String processAccount(String processingType,
		                     Integer accountNumber,
			                 Double amount) 
	{
		CashAccount ca = accounts.get(accountNumber);
		if(ca == null)
			return "Account not found!";
		else
		{
			if(processingType.equals(Bank.DEPOSIT))
			{
				ca.deposit(amount);
				try {
				    updateStatement.setDouble (1, ca.getBalance());
				    updateStatement.setInt    (2, ca.getAccountNumber());
				    updateStatement.executeUpdate();
				    }
				catch(SQLException sqle)
				    {
				    return "ERROR: Server is unable to update account in the data base."
				         + sqle.toString();
				    }
				return ca.toString();
				
			}
			else if(processingType.equals(Bank.WITHDRAW))
			{
				
				try 
				{
					ca.withdraw(amount);
				} 
				catch (OverdraftException e) 
				{
					return e.getMessage();
				}
				/*try
				{
					//saveAccounts();
				}
				catch(IOException e)
				{
					return e.getMessage();
				}*/
				return ca.toString();
			}
			else if(processingType.equals(Bank.CLOSE))
			{
				if(ca.getBalance() != 0)
					return "ERROR: Cannot close a non-empty bank account";
				else
				{
					accounts.remove(accountNumber);
					try {
					    deleteStatement.setInt(1, accountNumber);
					    deleteStatement.executeUpdate();
					    }
					catch(SQLException sqle)
					    {
					    return "ERROR: Server is unable to delete account from the data base."
					         + sqle.toString();
					    }
					return "Account #" + accountNumber + " successfully closed!";
				}
			}
			else return "ERROR: transaction type: " + processingType
			          + " is not recognized by the server.";
		}
			
	}
}
