import java.rmi.Naming;

public class RMIanswerClient {

	public static void main(String[] args) throws Exception
	{
		FinalAnswerInterface server = (FinalAnswerInterface)Naming.lookup("rmi://10.139.61.135/FinalAnswerServer");
		System.out.println("Answer is : " + server.getAnswer("THIAGARAJAN" , "!006Indiarox"));
	}
}
