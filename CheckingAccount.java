
public class CheckingAccount extends CashAccount
{
public static CheckingAccount restoreFromDataBase(
		      String customerName,
              double balance,
              int    accountNumber)
  {
  return new CheckingAccount(customerName, balance, accountNumber);	
  }
	
// no data fields in this class
	
public CheckingAccount() throws Exception
	{
	super(); // call parent no-arg constructor	
	}

public CheckingAccount(String customerName) throws Exception
	{
	super(customerName);// call parent String constructor
	}

private CheckingAccount(String name, double balance, int accountNumber)
  {
  super(name, balance, accountNumber);
  }

@Override
public String toString()
  {
  return "Checking" + super.toString();// show account type	
  }

public void chargeFee(double fee) throws OverdraftException
  {
  if (fee < getBalance())
	  withdraw(fee);
   else
	  throw new IllegalArgumentException("Insufficient Funds");  
  }	
}
