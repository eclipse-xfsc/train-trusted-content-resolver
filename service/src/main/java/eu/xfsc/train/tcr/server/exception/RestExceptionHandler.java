package eu.xfsc.train.tcr.server.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_EXTENDED;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import eu.xfsc.train.tcr.api.generated.model.Error;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * RestExceptionHandler translates RestExceptions to error responses according to the status that is set in
 * the application exception. Response content format: {"code" : "ExceptionType", "message" : "some exception message"}
 * Implementation of the {@link ResponseEntityExceptionHandler} exception.
 */
@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
  /**
   * Method handles the Client Exception.
   *
   * @param exception Thrown Client Exception.
   * @return The custom TCR application error with status code 400.
   */
  @ExceptionHandler({ClientException.class})
  protected ResponseEntity<Error> handleBadRequestException(ClientException exception) {
    log.info("handleBadRequestException; Bad Request error: {}", exception.getMessage());
    return new ResponseEntity<>(new Error("client_error", exception.getMessage()), BAD_REQUEST);
  }

  /**
   * Method handles the Constraint Violation Exception.
   *
   * @param exception Thrown Server Exception.
   * @return The custom TCR application error with status code 400.
   */
  @ExceptionHandler({ConstraintViolationException.class})
  protected ResponseEntity<Error> handleConstraintViolationException(ConstraintViolationException exception) {
    log.info("handleConstraintViolationException; Constraint Violation error: {}", exception.getMessage());
    return new ResponseEntity<>(new Error("constraint_violation_error", exception.getMessage()), BAD_REQUEST);
  }
  
  /**
   * Method handles the DNS resolution Exception.
   *
   * @param exception Thrown Server Exception.
   * @return The custom TCR application error with status code 504.
   */
  @ExceptionHandler({DnsException.class})
  protected ResponseEntity<Error> handleDnsException(DnsException exception) {
    log.info("handleDnsException; DNS error: {}", exception.getMessage());
    return new ResponseEntity<>(new Error("dns_error", exception.getMessage()), BAD_GATEWAY);
  }

  /**
   * Method handles the DID resolution Exception.
   *
   * @param exception Thrown Server Exception.
   * @return The custom TCR application error with status code 422.
   */
  @ExceptionHandler({DidException.class})
  protected ResponseEntity<Error> handleDidException(DidException exception) {
    log.info("handleDidException; DID error: {}", exception.getMessage());
    return new ResponseEntity<>(new Error("did_error", exception.getMessage()), NOT_EXTENDED);
  }

  /**
   * Method handles any other Runtime Exception.
   *
   * @param exception Thrown Server Exception.
   * @return The custom TCR application error with status code 500.
   */
  @ExceptionHandler({RuntimeException.class})
  protected ResponseEntity<Error> handleServerException(RuntimeException exception) {
    log.info("handleServerException; Runtime error: {}", exception.getMessage());
    return new ResponseEntity<>(new Error("server_error", exception.getMessage()), INTERNAL_SERVER_ERROR);
  }

}