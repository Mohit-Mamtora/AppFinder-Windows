package mohit;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;



public class Demo implements OnMyAppFind,OnSignal {

		public static void main(String[] args) throws UnknownHostException, InterruptedException {
		
		MyAppFinder myAppFinder;
		SignalHandler sh;
		
		Demo t=new Demo();
		sh=new SignalHandler(t);
		sh.startHandler();
		System.out.println(InetAddress.getLocalHost());
		myAppFinder=new MyAppFinder(t);
		myAppFinder.getSubnets();
		myAppFinder.ExecuteCommand("start");
		@SuppressWarnings("resource")
		Scanner scan=new Scanner(System.in);
		String temps=scan.nextLine();
		myAppFinder.ExecuteCommand(temps);
		sh.Shutdown();
	}

	@Override
	public void Onresponce(String name, String ip) {
		System.out.println("Responce Name : "+name+" ip :"+ip);
	}


	@Override
	public void OnCatch(String name, String ip) {
		//System.out.println("Signal catch name :"+name+" ip :"+ip);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//sh.Shutdown();
		//System.out.println("shutdown listioning");
	}

	@Override
	public void OnClose() {
		
	}

	@Override
	public void OnStart() {
		
	}

	@Override
	public void OnSignalSendback(int i) {
		
	}

	@Override
	public void OnListioning() {
		
	}

	@Override
	public void OnListioningStop() {
		
	}


	@Override
	public void OnSignalSendedTo(String GateWayIp) {
		System.out.println(GateWayIp+" ip signal sended");
	}

}
