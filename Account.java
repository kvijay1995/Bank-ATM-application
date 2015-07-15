import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public abstract class Account implements Serializable // a Bank Account Java Bean!
{
private static int lastAccountNumber; // inits to 0	
private synchronized static int getNextAccountNumber() throws Exception
  {
	  
  if (lastAccountNumber == 0)//not yet initialized from disk
	  // read lastAccountNumber from disk
     {
	 ObjectInputStream ois = new ObjectInputStream(
			                 new FileInputStream("LastAccountNumber.ser"));
	 lastAccountNumber = (Integer) ois.readObject();
	 ois.close();
     }
     
  lastAccountNumber++; // bump last to make next	  
  // save updated lastAccountNumber on disk
  ObjectOutputStream oos = new ObjectOutputStream(
                           new FileOutputStream("LastAccountNumber.ser"));
  oos.writeObject(lastAccountNumber);
  oos.close();
  return lastAccountNumber;	
  }
// A "handfull" of things to do when developing a Java bean
// 1. Declare data fields!
// 2. Provide "getter" and "setter" methods for the fields	
// 3. Provide a toString() to "introduce yourself".
// 4. Consider additional constructors.	
// 5. Give permission to have data "externalized"
//    (be type Serializable).	
	
private String customerName;
private int    accountNumber;

public  Account() throws Exception// default constructor
  {
  super();// call constructor of "parent" class "above me in the object	
  accountNumber = getNextAccountNumber();
  return;
  }

public Account(String customerName) throws Exception
   {
   this(); // call my default constructor	
   setCustomerName(customerName); 	
   }

protected Account(String name, int accountNumber) // "group setter!"
  {
  super(); // call my parent constructor (the one with no parms!)
  setCustomerName(name); // call setter from CTOR!
  this.accountNumber = accountNumber;
  }

public String toString()
   {  
   return "Account " + getAccountNumber() 
		   + " for " + getCustomerName();
   }

public String getCustomerName()
   {
	// Any "checking" of caller goes here 
   return customerName;
   }

public void setCustomerName(String customerName)
   {
   // any "edits" go here	
   this.customerName = customerName;	
   }

public int getAccountNumber()
  {
  return accountNumber;
  }
} 