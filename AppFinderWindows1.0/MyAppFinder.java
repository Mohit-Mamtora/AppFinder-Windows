/*
 * @author MOHIT MAMTORA 
 *   
 */

package mohit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;



public class MyAppFinder {

	private List<String>subnets;
	private boolean IsShutdown=true;
	private volatile boolean  IsListioning=false;
	public static final int PORT =9285;
	public static final int ListioningPORT =9286;
	private volatile DatagramSocket dss=null;
	private volatile DatagramSocket ds=null;
	private final OnMyAppFind OnMyAppFind;
	
	
	public MyAppFinder(OnMyAppFind OnMyAppFind){
		this.OnMyAppFind=OnMyAppFind;
	}
	public boolean getSubnets(){
		subnets=new ArrayList<>(2);
		try{
			Runtime runtime=Runtime.getRuntime();
			Process process=runtime.exec("ipconfig");
			BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
		
			String temp;
			while((temp=reader.readLine())!=null){
				if(temp.contains("IPv4 Address")){
					if(temp.trim().split(":").length==2){
						String t=temp.trim().split(":")[1].trim();
						subnets.add(t.substring(0,(t.indexOf('.',t.indexOf(".")+1))));
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return (subnets.size()>0);
	}
	
	public void ExecuteCommand(String cmd){
            switch (cmd) {
                case "shutdown":
                    stop();
                    break;
                case "start":
                    IsShutdown=false;
                    sendSignal();
        			SignalListioning();
                    break;
                case "sendsignal":
                    IsShutdown=false;
                    sendSignal();
                    
                    break;
                case "StartListion":
                    IsShutdown=false;
                    SignalListioning();
                    
                    break;
                default:
                    System.err.println("MyAppFinder: COMMAND NOT FOUND");
                    break;
            }
	}
	
	private int ii;
	private void sendSignal(){
		
		if(subnets.size()==0){return;}
		
		try {
			ds=new DatagramSocket();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		ii=subnets.size()-1;
		while(ii>=0)
		{
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						
						byte[] data = String.valueOf("").getBytes();
						
						StringBuilder sub=new StringBuilder(subnets.get(ii));
						int subnetSize=sub.length();
						
						DatagramPacket dp= new DatagramPacket(data, data.length);						
						dp.setPort(PORT);
						
						for (int i = 0; i <=254; i++) {
							for (int j = 0; j <=254; j++) {
									dp.setAddress(InetAddress.getByName(sub.append('.').append(i).append('.').append(j).toString()));
									ds.send(dp);
									sub.delete(subnetSize, sub.length());
									Thread.sleep(0,5);
							}
						}
						
						OnMyAppFind.OnSignalSendedTo(sub.toString());
						System.gc();
					} catch (Exception e) {
						//System.err.println(e.getMessage());
					}
				}
				
			},"Signal Sender").start();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ii--;
		}
		
		System.err.println("MyAppFinder: SENDING SIGNALS");
	}
	
	
	private void SignalListioning() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
						dss=new DatagramSocket(ListioningPORT);
						byte[]responceData=new byte[1];
						DatagramPacket dp=new DatagramPacket(responceData, responceData.length);
						while(!dss.isClosed()){
								dss.receive(dp);
								OnMyAppFind.Onresponce(dp.getAddress().getHostName(),dp.getAddress().getHostAddress());
						}
					} catch (IOException e) {
						if (e.getMessage().contains("Cannot bind")){
							System.err.println(e.getMessage());
						}
						IsListioning=false;
						OnMyAppFind.OnListioningStop();
					}
				}
			},"Listioning").start();
		IsListioning=true;
		OnMyAppFind.OnListioning();
		System.err.println("MyAppFinder: START LISTIONING ON "+ListioningPORT);
	}
	
	private void stop(){
		dss.close();
		ds.close();
		IsShutdown=true;
		System.err.println("MyAppFinder: SHUTINGDOWN");
	}
	
	public boolean IsShutdown(){
		return IsShutdown;
	}
	
	public boolean IsListioning(){
		return IsListioning;
	}

	
}
