package models;

import models.constants.GameConstants;


public class Player {

  private final String _name;
  private final Stockpile _stockpile;
  private final Buildings _buildings;

  public Player(String name) {
    _name = name;
    _stockpile = new Stockpile();
    _buildings = initBuildings();
  }

  private Buildings initBuildings() {
    Buildings buildings = new Buildings();
    GameConstants.NUM_BUILDINGS_PER_PLAYER.entrySet().stream()
        .forEach(en -> buildings.add(Building.ofType(en.getKey()), en.getValue()));
    return buildings;
  }

  public String getName() {
    return _name;
  }

  public Stockpile getStockpile() {
    return _stockpile;
  }

  public void exportResources(Stockpile stockpile) {
    _stockpile.remove(stockpile);
  }

  public void importResources(Stockpile stockpile) {
    _stockpile.add(stockpile);
  }

}
