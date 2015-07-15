public class SavingsAccount extends CashAccount
{
public static SavingsAccount restoreFromDataBase(
		      String customerName,
              double balance,
              int    accountNumber)
  {
  return new SavingsAccount(customerName, balance, accountNumber);	
  }
	
// no data fields in this class
	
public SavingsAccount() throws Exception
	{
	super(); // call parent no-arg constructor	
	}

public SavingsAccount(String customerName) throws Exception
	{
	super(customerName);// call parent String constructor
	}

private SavingsAccount(String name, double balance, int accountNumber) 
  {
  super(name, balance, accountNumber);
  }

@Override
public String toString()
  {
  return "Savings" + super.toString();// show account type	
  }

public void addInterest(double amount)
  {
  deposit(amount);
  }
}