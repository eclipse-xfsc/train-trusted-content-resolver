package eu.xfsc.train.tcr.server.exception;

public class DnsException extends TrainException {
    
	public DnsException(String message) {
		super(message);
	}

	public DnsException(Throwable cause) {
		super(cause);
	}

	public DnsException(String message, Throwable cause) {
		super(message, cause);
	}
}

