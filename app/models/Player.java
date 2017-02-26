package models;

import exceptions.NotEnoughResourcesException;
import models.constants.Color;
import models.constants.GameConstants;
import play.Logger;


public class Player {
  private static final Logger.ALogger _logger = Logger.of(Player.class);

  private final String name;
  private Color color;

  private final Stockpile stockpile;
  private final Buildings buildings;
  private final DevCardStack devCardStack;

  private int victoryPoints;

  public Player(String name, Color color) {
    this.name = name;
    this.color = color;
    victoryPoints = 0;
    stockpile = new Stockpile();
    buildings = initBuildings();
    devCardStack = new DevCardStack();
  }

  private Buildings initBuildings() {
    Buildings buildings = new Buildings();
    GameConstants.NUM_BUILDINGS_PER_PLAYER.entrySet().stream()
        .forEach(en -> buildings.add(new Building(en.getKey(), color), en.getValue()));
    return buildings;
  }

  public Building removeBuilding(BuildingType type) {
    Building dummy = new Building(type, color);
    _logger.info("Buildings: " + buildings);
    System.out.println("Buildings: " + buildings);
    buildings.remove(dummy);
    return dummy;
  }

  public void awardVictoryPoint() {
    victoryPoints++;
  }

  public int getVictoryPoints() {
    return victoryPoints;
  }

  public String getName() {
    return name;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Stockpile getStockpile() {
    return stockpile;
  }

  public void exportResources(Stockpile stockpile) throws NotEnoughResourcesException {
    this.stockpile.remove(stockpile);
  }

  public void importResources(Stockpile stockpile) {
    this.stockpile.add(stockpile);
  }

  public void addDevCard(DevCard type) {

  }

  @Override
  public String toString() {
    return name;
  }

}
