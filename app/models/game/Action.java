package models.game;

import com.fasterxml.jackson.databind.JsonNode;


public class Action {
  private final ActionType type;
  private final JsonNode data;

  public Action(ActionType type, JsonNode data) {
    this.type = type;
    this.data = data;
  }
}
