public class Client extends Task{
	Client(Broker b, Runnable r){
		super(b,r);
	}
	
	public void run() {
		Broker broker = new Broker("Client");
        try {
            Channel ch = broker.connect("main", 1234);
            String msg = "Hello Wrold!";
            ch.write(msg.getBytes(), 0, msg.length());
            ch.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
