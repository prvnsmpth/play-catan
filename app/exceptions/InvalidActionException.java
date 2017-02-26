package exceptions;

public class InvalidActionException extends Exception {
  public InvalidActionException() {
    super();
  }

  public InvalidActionException(String msg) {
    super(msg);
  }
}
