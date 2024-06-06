

/**
 * Server information.
 */
public class ServerSettings {

	private String host_; // server host
	private int port_; // server port

	/**
	 * Initialize with default values.
	 */
	public ServerSettings () {
		this("localhost",9050);
	}

	/**
	 * Initialize with the specified values.
	 * 
	 * @param host
	 *          server host
	 * @param port
	 *          server port
	 */
	public ServerSettings ( String host, int port ) {
		host_ = host;
		port_ = port;
	}

	/**
	 * Get the server host.
	 * 
	 * @return server host
	 */
	public String getHost () {
		return host_;
	}

	/**
	 * Get the server port.
	 * 
	 * @return server port
	 */
	public int getPort () {
		return port_;
	}

}
