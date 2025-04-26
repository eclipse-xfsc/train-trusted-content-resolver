package eu.xfsc.train.tcr.server.exception;

public class ClientException extends TrainException {

	public ClientException(String message) {
		super(message);
	}

	public ClientException(Throwable cause) {
		super(cause);
	}

	public ClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
