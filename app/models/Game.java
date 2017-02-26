package models;

import exceptions.NotEnoughDevCardsException;
import exceptions.NotEnoughResourcesException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.board.Board;
import models.board.BoardGenerator;
import models.board.coords.Coords;
import models.constants.Color;
import models.constants.DevCardType;
import models.constants.GameConstants;
import models.constants.ResourceType;
import models.stage.GamePhase;
import org.apache.commons.lang3.tuple.Pair;


public class Game {

  private Long id;

  private final List<Player> players;

  private final Board board;

  private final Stockpile bank;
  private final DevCardStack bankDevCardStack;

  private final List<Pair<Integer, Integer>> rollHistory;
  private final Map<Color, Player> playerColor;

  private int currentPlayerPos = 0;

  private GamePhase phase;

  public Game() {
    players = new ArrayList<>();
    board = generateBoard();
    rollHistory = new ArrayList<>();
    bank = initBank();
    playerColor = new HashMap<>();
    bankDevCardStack = initDevCardStack();
    phase = GamePhase.SETUP_ROUND_1;
  }

  private Stockpile initBank() {
    Stockpile bank = new Stockpile();

    // Add resources
    Arrays.stream(ResourceType.values())
        .forEach(t -> bank.add(new Resource(t), GameConstants.MAX_NUM_RESOURCES_PER_TYPE));

    return bank;
  }

  private DevCardStack initDevCardStack() {
    DevCardStack stack = new DevCardStack();
    GameConstants.NUM_DEV_CARDS.entrySet().stream()
        .forEach(en -> stack.add(new DevCard(en.getKey()), en.getValue()));
    return stack;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void addPlayer(String playerName) {
    if (players.size() == GameConstants.MAX_PLAYERS) {
      throw new IllegalStateException("No room for more.");
    }
    Color color = Color.fromPlayerPos(players.size());
    Player player = new Player(playerName, color);
    players.add(player);
    playerColor.put(color, player);
  }

  public void endTurn() {
    currentPlayerPos = (currentPlayerPos + 1) % players.size();
  }

  public Pair<Integer, Integer> roll() throws NotEnoughResourcesException {
    int first = (int) Math.ceil(Math.random() * 6);
    int second = (int) Math.ceil(Math.random() * 6);
    Pair<Integer, Integer> rolled = Pair.of(first, second);
    try {
      handleRoll(rolled.getLeft() + rolled.getRight());
      return rolled;
    } finally {
      rollHistory.add(rolled);
    }
  }

  public void handleRoll(int roll) throws NotEnoughResourcesException {
    // TODO: Handle 7

    Map<Color, Stockpile> produce = board.produce(roll);
    boolean canProduce = produce.entrySet().stream()
        .map(en -> {
          try {
            bank.remove(en.getValue());
            return true;
          } catch (NotEnoughResourcesException e) {
            // No one gets anything
            return false;
          }
        })
        .reduce(true, (a, b) -> a && b);

    if (!canProduce) {
      throw new NotEnoughResourcesException("Bank does not have enough resources");
    }

    // Distribute proceeds among players
    produce.entrySet().stream()
        .forEach(en -> {
          Player player = playerColor.get(en.getKey());
          player.importResources(en.getValue());
        });
  }

  /**
   * Trade with the bank.
   *
   * Imported/exported piles are from the perspective of the current player.
   *
   * @param imported
   * @param exported
   */
  public void maritimeTrade(Stockpile imported, Stockpile exported) throws NotEnoughResourcesException {
    Player currentPlayer = getCurrentPlayer();

    currentPlayer.exportResources(exported);
    bank.add(exported);

    bank.remove(imported);
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
  public void playerTrade(Player player, Stockpile imported, Stockpile exported) throws NotEnoughResourcesException {
    Player currentPlayer = getCurrentPlayer();

    currentPlayer.exportResources(exported);
    player.importResources(exported);

    player.exportResources(imported);
    currentPlayer.importResources(imported);
  }

  public Player getCurrentPlayer() {
    if (players.size() == 0) {
      return null;
    }
    return players.get(currentPlayerPos);
  }

  public int getNumPlayers() {
    return players.size();
  }

  public void build(BuildingType buildingType, Coords coords) throws NotEnoughResourcesException {
    Player currentPlayer = getCurrentPlayer();
    Building building = currentPlayer.removeBuilding(buildingType);

    if (currentPlayer.getVictoryPoints() >= 2) {
      Stockpile buildingCost = building.getBuildingCost();
      currentPlayer.exportResources(buildingCost);
      bank.add(buildingCost);
    }

    if (building.getType() != BuildingType.ROAD) {
      currentPlayer.awardVictoryPoint();
    }

    board.build(building, coords);
  }

  public void buyDevCard(DevCardType cardType) throws NotEnoughResourcesException, NotEnoughDevCardsException {
    Player currentPlayer = getCurrentPlayer();
    currentPlayer.exportResources(GameConstants.DEV_CARD_COST);
    bank.add(GameConstants.DEV_CARD_COST);
    DevCard card = bankDevCardStack.remove(cardType);
    currentPlayer.addDevCard(card);
  }

  private Board generateBoard() {
    return BoardGenerator.generate();
  }

}
