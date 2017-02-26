package exceptions;

import models.DevCard;


public class NotEnoughDevCardsException extends Exception {
  private DevCard _card;

  public NotEnoughDevCardsException() {
    super();
  }

  public NotEnoughDevCardsException(String msg) {
    super(msg);
  }

  public NotEnoughDevCardsException(DevCard requested) {
    super();
    _card = requested;
  }

  public DevCard getRequested() {
    return _card;
  }
}
