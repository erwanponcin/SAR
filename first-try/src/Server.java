
public class Server extends Task{
	Server(Broker b, Runnable r){
		super(b,r);
	}
	
	public void run() {
		Broker broker = new Broker("Server");
        try {
            Channel ch = broker.accept(1234);
            byte[] buffer = new byte[64];
            int n = ch.read(buffer, 0, buffer.length);
            System.out.println("Serveur a reçu : " + new String(buffer, 0, n));
            ch.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
