import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class TellerClient implements ActionListener
{
// -- STATIC STUFF -----------------------------------------------------	
public static void main(String[] args) // load myself!
    {
	String newLine = System.getProperty("line.separator");
	if (args.length != 2)
        {
	    System.out.println("Server address must be provided as command line parameter #1"
	    	   + newLine + "and the server application name as command line parameter #2");
	    return;
        }
	String serverAddress         = args[0];
	String serverApplicationName = args[1];
	
	try {                   
	    new TellerClient(serverAddress,serverApplicationName); 
	    }                    
    catch(Exception e)
        {
    	System.out.println(e);
        }
    }


//-- OBJECT STUFF --------------------------------------------------------

// GUI objects
private JFrame      window                = new JFrame("BANK TELLER");	
private JTextField  accountTextField      = new JTextField(8); 
private JTextField  amountTextField       = new JTextField(8);
private JTextField  customerNameTextField = new JTextField(8);
private JLabel      accountLabel          = new JLabel("Enter account #");
private JLabel      amountLabel           = new JLabel("Enter $ amount");
private JLabel      customerNameLabel     = new JLabel("Enter customer name Last,First");
private JButton     closeButton           = new JButton("CloseAccount");
private JButton     clearButton           = new JButton("Clear");
private JButton     showByNumberButton    = new JButton("Show Account");
private JButton     showByNameButton      = new JButton("Show All Accounts");
private JButton     depositButton         = new JButton("Deposit");
private JButton     withdrawButton        = new JButton("Withdraw");
private JButton     openNewCheckingButton = new JButton("New Checking");
private JButton     openNewSavingsButton  = new JButton("New Savings");
private JTextArea   displayTextArea       = new JTextArea();
private JScrollPane displayScrollPane     = new JScrollPane(displayTextArea);
private JPanel      topPanel              = new JPanel();
private JPanel      bottomPanel           = new JPanel();

// Other "instance" (program) variables
TellerInterface server = null; 

//=======================================================================
public TellerClient(String serverAddress,
		                      String serverApplicationName)
                              throws Exception
    {// My CONSTRUCTOR is called by the loader, and
	 // the loader is called by my loading program's thread.
	// Build my GUI!
	topPanel.add(accountLabel);
	topPanel.add(accountTextField);
	topPanel.add(showByNumberButton);
	topPanel.add(amountLabel);
	topPanel.add(amountTextField);
	topPanel.add(depositButton);
	topPanel.add(withdrawButton);
	topPanel.add(closeButton);

	window.getContentPane().add(topPanel, "North");
	
	window.getContentPane().add(displayScrollPane, "Center");

	bottomPanel.add(clearButton);
	bottomPanel.add(customerNameLabel);
	bottomPanel.add(customerNameTextField);
	bottomPanel.add(showByNameButton);
	bottomPanel.add(openNewCheckingButton);
	bottomPanel.add(openNewSavingsButton);
	window.getContentPane().add(bottomPanel, "South");
	
	// Set attributes of GUI objects
	displayTextArea.setEditable(false); // keep cursor out
	displayTextArea.setFont(new Font("default", Font.BOLD, 20));
	showByNumberButton.setBackground(Color.black);
	showByNameButton.setBackground(Color.black);
	showByNumberButton.setForeground(Color.yellow);
	showByNameButton.setForeground(Color.yellow);
	depositButton.setBackground(Color.green);
	withdrawButton.setBackground(Color.green);
	clearButton.setBackground(Color.yellow);
	closeButton.setBackground(Color.red);
    openNewCheckingButton.setBackground(Color.cyan);
	openNewSavingsButton.setBackground(Color.cyan);
	
	// Sign up for / activate event notification
	showByNumberButton.addActionListener(this); // "call me!"
	showByNameButton.addActionListener(this); // "call me!"
	openNewCheckingButton.addActionListener(this); // "call me!"
	openNewSavingsButton.addActionListener(this); // "call me!"
	depositButton.addActionListener(this); // "call me!"
	withdrawButton.addActionListener(this); // "call me!"
	clearButton.addActionListener(this); // "call me!"
	closeButton.addActionListener(this); // "call me!"
	
    // Show window	
	window.setLocation(10, 10); // horizontal, vertical
	window.setSize(900, 400); // width,height in pixels
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setVisible(true);
    
    // Establish connection to the bank server!
    try {
    	server = (TellerInterface)
    	          Naming.lookup("rmi://"
    	        		  + serverAddress
    	        		  + "/"
    	        		  + serverApplicationName);
        System.out.println("Connected to " + serverApplicationName
        		         + " at "          + serverAddress);
        }
    catch(Exception e)
        {
    	displayTextArea.setText("Problems connecting to bank server at "
		           + serverAddress  
		           + " with app name " + serverApplicationName 
		           + " " + e);
    	throw e;
        }
    }

//============================================================================
public void actionPerformed(ActionEvent ae) // buttons etc. call me here! 
  {
  displayTextArea.setText("");//clear previous data or error msg 

  if (ae.getSource() == clearButton)
     {
     accountTextField.setText("");
	 amountTextField.setText("");
	 customerNameTextField.setText("");
	 displayTextArea.setText("");
	 return;
	 }
  
  try{
	 if(ae.getSource()==closeButton)          processAccount(Bank.CLOSE);
else if(ae.getSource()==depositButton)        processAccount(Bank.DEPOSIT);
else if(ae.getSource()==withdrawButton)       processAccount(Bank.WITHDRAW);
else if(ae.getSource()==openNewCheckingButton)openNewAccount(Bank.CHECKING);
else if(ae.getSource()==openNewSavingsButton) openNewAccount(Bank.SAVINGS);
else if(ae.getSource()==showByNumberButton)   showAccount();
else if(ae.getSource()==showByNameButton)     showAccounts();
else displayTextArea.setText("PROGRAM ERROR: " + ae.getSource() + " not recognized in actionPerformed() in TellerClient.");	 
     }
  catch(IllegalArgumentException iae)
     {
	 displayTextArea.setText(iae.getMessage());
     }
  catch(RemoteException re)
     {
	 displayTextArea.setText(re.toString());
     }
  }

//============================================================================= 
//
//     BUTTON PROCESSING METHODS 
//
//=============================================================================
private void processAccount(String processType) throws IllegalArgumentException, RemoteException
  {
  if (processType.equals(Bank.DEPOSIT))	
     {
	 System.out.println("deposit button was pushed");
	 notCustomerName(processType);
	 int    account = getAccountNumber();
	 double amount  = getAmount();
	 displayTextArea.setText(server.processAccount(Bank.DEPOSIT, account, amount));
     }

  if (processType.equals(Bank.WITHDRAW))
     {
     System.out.println("withdraw button was pushed");
	 notCustomerName(processType);
     int    account = getAccountNumber();
     double amount  = getAmount();
     // call the server here, passing required parameters from GUI.
     displayTextArea.setText(server.processAccount(processType, account, amount));
     }

  if (processType.equals(Bank.CLOSE))
     {
     System.out.println("close button was pushed");
	 notAmount(processType);
     int account = getAccountNumber();
     displayTextArea.setText(server.processAccount(Bank.CLOSE, account,0.0));
     }
  }

//=========================================================================
private void openNewAccount(String accountType) throws IllegalArgumentException, RemoteException
  {
  notAccount("openNewAccount");
  notAmount("openNewAccount");
  String customerName = getCustomerName();
	
  if (accountType.equals(Bank.CHECKING))
     { 
     System.out.println("newChecking button was pushed");
     displayTextArea.setText(server.openNewAccount(accountType, customerName));
     }

  if (accountType.equals(Bank.SAVINGS))
     {
     System.out.println("newSavings button was pushed");
     displayTextArea.setText(server.openNewAccount(accountType, customerName));
     }
  }

//=========================================================================
private void showAccount() throws IllegalArgumentException, RemoteException
  {
  notAmount("show account");
  notCustomerName("show account by number");
  int account = getAccountNumber();

  System.out.println("showAccountByNumber button was pushed");
  displayTextArea.setText(server.showAccount(account));
  }

//==========================================================================
private void showAccounts() throws IllegalArgumentException, RemoteException
  {
  notAmount("show accounts");
  notAccount("show accounts by customer name");
  System.out.println("showAccountsByName button was pushed");
  // In a departure from the usual edit procedure,
  // we will NOT call the getCustomerName() edit 
  // method to read the customerName from the GUI
  // because we want to allow the teller to enter a
  // SHORT name (that may not even contain a comma)
  // for the customer accounts search.
  // (So read the GUI from here to get customerName)
  String customerName = customerNameTextField.getText().trim();
  if (customerName.length() == 0)
      throw new IllegalArgumentException("Customer name required (may be a 'short' name).");
  displayTextArea.setText(server.showAccounts(customerName));
  }

//=============================================================================
//
//    EDIT METHODS for GUI FIELDS
//
//========================================================================
private String getCustomerName() throws IllegalArgumentException
  {
  System.out.println("getCustomerName() edit method was called");
  // get from GUI, check, convert, return value.
  String customerName = customerNameTextField.getText().trim();
  if (customerName.length() == 0)
	  throw new IllegalArgumentException("Customer name is required.");
  if (!customerName.contains(","))
	  throw new IllegalArgumentException("Customer name must be entered in form Last,First");
  if (customerName.startsWith(",") || customerName.endsWith(","))
	  throw new IllegalArgumentException("Customer name must not start or end with a comma.");
  int commaOffset = customerName.indexOf(",");
  int nextCommaOffset = customerName.indexOf(",",commaOffset+1);
  if (nextCommaOffset != -1)
	  throw new IllegalArgumentException("Customer name must not contain more than one comma.");
  return customerName;
  }

//=========================================================================
private int getAccountNumber() throws IllegalArgumentException
  {
  System.out.println("getAccountNumber() edit method was called");
  // get from GUI, check, convert, return value.
  String accountNumber = accountTextField.getText().trim();
  if (accountNumber.length() == 0)
	  throw new IllegalArgumentException("Account number required.");
  int account;
  try {
      account = Integer.parseInt(accountNumber);
      if (account < 1)
    	  throw new IllegalArgumentException("Account number must be a positive number.");
      }
  catch(NumberFormatException nfe)
      {
	  throw new IllegalArgumentException("Account number must be a whole number.");
      }
  return account;
  }

//==========================================================================
private double getAmount() throws IllegalArgumentException
  {
  System.out.println("getAmount() edit method was called");
  // get from GUI, check, convert, return value.	
  String amountString = amountTextField.getText().trim();
  if (amountString.length() == 0)
	  throw new IllegalArgumentException("Amount is required.");
  if (amountString.startsWith("0"))
	  throw new IllegalArgumentException("Amount must not have a leading 0");
  if (amountString.startsWith("$"))
	  amountString = amountString.substring(1).trim();
  if (amountString.contains(","))
	  throw new IllegalArgumentException("Amount must not contain commas.");
  int decimalOffset = amountString.indexOf(".");
  if (decimalOffset > -1) // found a decimal point
     {
	 if (amountString.length() != decimalOffset + 3)
   	     throw new IllegalArgumentException("If a decimal point is present in the amount it must be followed by 2 decimal digits.");
     }
  double amount;
  try {
      amount = Double.parseDouble(amountString);
      if ((amount < 0) || (amount == 0))
    	  throw new IllegalArgumentException("Amount must be positive.");
      }
  catch(NumberFormatException nfe)
      {
	  throw new IllegalArgumentException("Account number must be a whole number.");
      }
  return amount;	
  }

//=========================================================
private void notAmount(String whatFunction) throws IllegalArgumentException
  {
  String amount = amountTextField.getText().trim();
  if (amount.length() == 0)
	  return;
   else
	  throw new IllegalArgumentException("An amount should not be provided for " + whatFunction);
  }
private void notAccount(String whatFunction) throws IllegalArgumentException
  {
  String account = accountTextField.getText().trim();
  if (account.length() == 0)
	  return;
   else
	  throw new IllegalArgumentException("Account number should not be provided for " + whatFunction);
  }
private void notCustomerName(String whatFunction) throws IllegalArgumentException
  {
  String customerName = customerNameTextField.getText().trim();
  if (customerName.length() == 0)
	  return;
   else
	  throw new IllegalArgumentException("Customer name should not be provided for " + whatFunction);
  }
}
