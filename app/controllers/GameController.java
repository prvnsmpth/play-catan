package controllers;

import models.Game;
import models.Player;
import org.apache.commons.lang3.tuple.Pair;
import play.mvc.Controller;
import play.mvc.Result;


public class GameController extends Controller {

  private Game game;

  public Result newGame() {
    game = new Game();
    return ok("New game started.");
  }

  public Result addPlayer(String name) {
    game.addPlayer(new Player(name));
    return ok("Added");
  }

  public Result start() {
    return ok();
  }

  public Result roll() {
    Pair<Integer, Integer> rolled = game.roll();
    return ok(String.format("Rolled (%d, %d)", rolled.getLeft(), rolled.getRight()));
  }

  public Result gameState() {
    Player player = game.getCurrentPlayer();
    return ok(String.format("Current player: %s", player.getName()));
  }

  public Result endTurn() {
    game.endTurn();
    return redirect("game");
  }
}
