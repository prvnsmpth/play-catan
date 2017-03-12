package controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import exceptions.NotEnoughResourcesException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.inject.Inject;
import models.BuildingType;
import models.Game;
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
  private static final Logger.ALogger logger = Logger.of(GameController.class);

  private Game game;
  private ActorRef gameActor;
  private ActorSystem actorSystem;

  @Inject
  public GameController(ActorSystem actorSystem) {
    refreshGameState();
    if (game == null) {
      logger.info("Error refreshing game state from local file, initializing new game.");
      game = new Game();
      saveGameState();
    }

    this.actorSystem = actorSystem;
  }

  private void saveGameState() {
    File gameFile = new File("game.json");
    try {
      FileOutputStream fos = new FileOutputStream(gameFile);
      JsonNode gameJson = Json.toJson(game);
      fos.write(gameJson.toString().getBytes());
      fos.close();
    } catch (IOException e) {
      logger.error("Failed to write game state.", e);
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
      game = Json.fromJson(gameJson, Game.class);
    } catch (IOException e) {
      logger.error("Failed to load game state.", e);
    }
  }

  public Result newGame() {
    game = new Game();
    saveGameState();
    return redirect("game");
  }

  @BodyParser.Of(BodyParser.Json.class)
  public Result addPlayer() {
    JsonNode json = request().body().asJson();
    String name = json.get("name").textValue();
    try {
      game.addPlayer(name);
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
      game.build(buildingType, coords);
      saveGameState();
      ObjectMapper mapper = Json.mapper();
      ObjectWriter writer = mapper.writer(new SimpleFilterProvider().addFilter("filter",
          SimpleBeanPropertyFilter.filterOutAllExcept("currentPlayerPos")));
      return ok(Json.parse(writer.writeValueAsString(game)));
    } catch (NotEnoughResourcesException | IllegalStateException | JsonProcessingException e) {
      refreshGameState();
      logger.error("Something went wrong", e);
      return badRequest("Illegal operation: " + e.getMessage());
    }
  }

  public Result start() {
    return ok();
  }

  public Result roll() {
    try {
      Pair<Integer, Integer> rolled = game.roll();
      saveGameState();
      return ok(String.format("Rolled (%d, %d)", rolled.getLeft(), rolled.getRight()));
    } catch (NotEnoughResourcesException ex) {
      return ok("Not enough resources :(");
    }
  }

  public Result play() {
    return ok(index.render(game));
  }

  public Result gameState(Integer id) {
    return ok(Json.toJson(game));
  }

  public Result boardState(Integer gameId) {
    return ok(Json.toJson(game.getBoard()));
  }

  public Result updateBoard(Integer gameId) {
    return ok();
  }

  public Result endTurn() {
    game.endTurn();
    saveGameState();
    return redirect("game");
  }
}
