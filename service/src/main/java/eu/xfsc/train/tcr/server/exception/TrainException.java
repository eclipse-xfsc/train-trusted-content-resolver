package eu.xfsc.train.tcr.server.exception;

/**
 * ServiceException is the main exception that can be thrown during the operations of Federated Catalogue
 * server application.
 * Implementation of the {@link RuntimeException} exception.
 */
public class TrainException extends RuntimeException {
  /**
   * Constructs a new Train Exception with the specified detail message.
   *
   * @param message Detailed message about the thrown exception.
   */
  public TrainException(String message) {
    super(message);
  }

  /**
   * Constructs a new Train exception with the specified cause.
   *
   * @param cause Case of the thrown exception. (A null value is permitted, and indicates that the cause is unknown.)
   */
  public TrainException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new Train exception with the specified detail message and cause.
   *
   * @param message Detailed message about the thrown exception.
   * @param cause Cause of the thrown exception. (A null value is permitted, and indicates that the cause is unknown.)
   */
  public TrainException(String message, Throwable cause) {
    super(message, cause);
  }

}
