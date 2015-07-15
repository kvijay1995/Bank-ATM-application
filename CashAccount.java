import java.math.BigDecimal;
import java.math.MathContext;


public abstract class CashAccount extends Account
{
private double balance;
public CashAccount() throws Exception
	{
		// TODO Auto-generated constructor stub
	}

public CashAccount(String customerName) throws Exception
	{
	super(customerName);
	}

protected CashAccount(String name, double balance, int accountNumber) 
  {
  super(name, accountNumber);
  this.balance = balance;
  }

@Override
public String toString()
  {
/*  
  String balanceString = String.valueOf(getBalance());
  // ensure 2 decimal digits 
  int decimalPointOffset = balanceString.indexOf(".");
  if (decimalPointOffset < 0) // no decimal point
	  balanceString += ".00";
   else // balance contains a decimal point
     {
	 if (balanceString.length()-decimalPointOffset==1)
		  balanceString += "00";
	 if (balanceString.length()-decimalPointOffset==2)
		  balanceString += "0";
	 if (balanceString.length()-decimalPointOffset>3)
		  balanceString = balanceString.substring(0,decimalPointOffset+3);
     }
*/
  BigDecimal  balanceBD = new BigDecimal(balance,MathContext.DECIMAL64);
  balanceBD = balanceBD.setScale(2,BigDecimal.ROUND_UP);//scale (2) is # of digits to right of decimal point.
  String balanceString = balanceBD.toPlainString();// no exponents	

  return super.toString() + " $" + balanceString;
  }

public synchronized void deposit(double amount)
   { 
   balance += amount;	
   }

public synchronized void withdraw(double amount) throws OverdraftException
   {
   if (amount <= balance)	
       balance -= amount;
    else
      throw new OverdraftException("Insufficient Funds");	
   }

public double getBalance()
   {
	return balance;
    }

}