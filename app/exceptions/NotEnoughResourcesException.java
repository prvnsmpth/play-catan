package exceptions;

import models.Stockpile;


public class NotEnoughResourcesException extends Exception {

  private Stockpile _available;
  private Stockpile _requested;

  public NotEnoughResourcesException() {
    super();
  }

  public NotEnoughResourcesException(String msg) {
    super(msg);
  }

  public NotEnoughResourcesException(Stockpile available, Stockpile requested) {
    super();
    _available = available;
    _requested = requested;
  }

  public Stockpile getRequested() {
    return _requested;
  }

  public Stockpile getAvailable() {
    return _available;
  }

}
