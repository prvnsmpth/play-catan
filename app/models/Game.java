package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.board.Board;
import models.constants.GameConstants;
import org.apache.commons.lang3.tuple.Pair;


public class Game {

  private final List<Player> _players;
  private final Board _board;
  private final Stockpile _bank;

  private final List<Pair<Integer, Integer>> _rollHistory;

  private int _currentPlayerPos = 0;

  public Game() {
    _players = new ArrayList<>();
    _board = generateBoard();
    _rollHistory = new ArrayList<>();
    _bank = initBank();
  }

  private Stockpile initBank() {
    Stockpile bank = new Stockpile();

    // Add resources
    Arrays.stream(ResourceType.values())
        .forEach(t -> bank.add(new Resource(t), GameConstants.MAX_NUM_RESOURCES_PER_TYPE));

    return bank;
  }

  public void addPlayer(Player player) {
    if (_players.size() == GameConstants.MAX_PLAYERS) {
      throw new IllegalStateException("No room for more.");
    }
    _players.add(player);
  }

  public void endTurn() {
    _currentPlayerPos = (_currentPlayerPos + 1) % _players.size();
  }

  public void addRoll(Pair<Integer, Integer> roll) {
    _rollHistory.add(roll);
  }

  public Pair<Integer, Integer> roll() {
    int first = (int) Math.ceil(Math.random() * 6);
    int second = (int) Math.ceil(Math.random() * 6);
    Pair<Integer, Integer> rolled = Pair.of(first, second);
    _rollHistory.add(rolled);
    return rolled;
  }

  /**
   * Trade with the bank.
   *
   * Imported/exported piles are from the perspective of the current player.
   *
   * @param imported
   * @param exported
   */
  public void maritimeTrade(Stockpile imported, Stockpile exported) {
    Player currentPlayer = getCurrentPlayer();

    currentPlayer.exportResources(exported);
    _bank.add(exported);

    _bank.remove(imported);
    currentPlayer.importResources(imported);
  }

  /**
   * Trade between players.
   *
   * Imported/exported piles are from the perspective of the current player.
   *
   * @param player
   * @param imported
   * @param exported
   */
  public void playerTrade(Player player, Stockpile imported, Stockpile exported) {
    Player currentPlayer = getCurrentPlayer();

    currentPlayer.exportResources(exported);
    player.importResources(exported);

    player.exportResources(imported);
    currentPlayer.importResources(imported);
  }

  public Player getCurrentPlayer() {
    return _players.get(_currentPlayerPos);
  }

  private Board generateBoard() {
    return new Board();
  }

}
