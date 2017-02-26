package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import exceptions.NotEnoughResourcesException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import models.BuildingType;
import models.Game;
import models.Player;
import models.board.coords.Coords;
import models.board.coords.EdgeCoords;
import models.board.coords.VertexCoords;
import org.apache.commons.lang3.tuple.Pair;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class GameController extends Controller {
  private static final Logger.ALogger _logger = Logger.of(GameController.class);

  private Game _game;

  public GameController() {
    refreshGameState();
    if (_game == null) {
      _logger.info("Error refreshing game state from local file, initializing new game.");
      _game = new Game();
      saveGameState();
    }
  }

  private void saveGameState() {
    File gameFile = new File("game.json");
    try {
      FileOutputStream fos = new FileOutputStream(gameFile);
      JsonNode gameJson = Json.toJson(_game);
      fos.write(gameJson.toString().getBytes());
      fos.close();
    } catch (IOException e) {
      _logger.error("Failed to write game state.", e);
    }
  }

  private void refreshGameState() {
    File gameFile = new File("game.json");
    try {
      FileInputStream fis = new FileInputStream(gameFile);
      byte[] gameState = new byte[(int) gameFile.length()];
      fis.read(gameState);
      fis.close();
      JsonNode gameJson = Json.parse(gameState);
      _game = Json.fromJson(gameJson, Game.class);
    } catch (IOException e) {
      _logger.error("Failed to load game state.", e);
    }
  }

  public Result newGame() {
    _game = new Game();
    saveGameState();
    return redirect("game");
  }

  @BodyParser.Of(BodyParser.Json.class)
  public Result addPlayer() {
    JsonNode json = request().body().asJson();
    String name = json.get("name").textValue();
    try {
      _game.addPlayer(name);
      saveGameState();
      return ok();
    } catch (IllegalStateException e) {
      refreshGameState();
      return badRequest(e.getMessage());
    }
  }

  @BodyParser.Of(BodyParser.Json.class)
  public Result build() {
    JsonNode data = request().body().asJson();
    BuildingType buildingType = BuildingType.fromValue(data.get("type").textValue());
    Coords coords;
    if (buildingType.equals(BuildingType.ROAD)) {
      coords = Json.fromJson(data.get("coords"), EdgeCoords.class);
    } else {
      coords = Json.fromJson(data.get("coords"), VertexCoords.class);
    }
    try {
      _game.build(buildingType, coords);
      saveGameState();
      return ok();
    } catch (NotEnoughResourcesException | IllegalStateException e) {
      refreshGameState();
      return badRequest("Illegal operation: " + e.getMessage());
    }
  }

  public Result start() {
    return ok();
  }

  public Result roll() {
    try {
      Pair<Integer, Integer> rolled = _game.roll();
      saveGameState();
      return ok(String.format("Rolled (%d, %d)", rolled.getLeft(), rolled.getRight()));
    } catch (NotEnoughResourcesException ex) {
      return ok("Not enough resources :(");
    }
  }

  public Result game() {
    return ok(game.render(_game));
  }

  public Result gameState() {
    return ok(Json.toJson(_game));
  }

  public Result endTurn() {
    _game.endTurn();
    saveGameState();
    return redirect("game");
  }
}
