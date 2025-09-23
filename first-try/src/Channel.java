public abstract class Channel {
	int read(byte[] bytes, int offset, int length);
	int write(byte[] bytes, int offset, int length);
	void disconnect();
	boolean disconnected();
}
