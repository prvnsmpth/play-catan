package models;

public class Action {
  private final ActionType _type;

  public Action(ActionType type) {
    _type = type;
  }

  public ActionType getType() {
    return _type;
  }
}
