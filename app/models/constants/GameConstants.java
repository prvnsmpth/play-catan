package models.constants;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import models.BuildingType;
import models.Stockpile;


public class GameConstants {
  public static final int MAX_PLAYERS = 4;
  public static final int MAX_NUM_RESOURCES_PER_TYPE = 19;

  public static final Map<DevCardType, Integer> NUM_DEV_CARDS = ImmutableMap.<DevCardType, Integer> builder()
      .put(DevCardType.KNIGHT, 14)
      .put(DevCardType.MONOPOLY, 2)
      .put(DevCardType.YEAR_OF_PLENTY, 2)
      .put(DevCardType.INVENTION, 2)
      .put(DevCardType.VICTORY_POINT, 5)
      .build();

  public static final Map<BuildingType, Integer> NUM_BUILDINGS_PER_PLAYER =
      ImmutableMap.<BuildingType, Integer> builder()
        .put(BuildingType.ROAD, 15)
        .put(BuildingType.SETTLEMENT, 5)
        .put(BuildingType.CITY, 4)
      .build();

  public static final Map<ResourceType, Integer> NUM_HEXES_PER_RESOURCE = ImmutableMap.<ResourceType, Integer> builder()
      .put(ResourceType.WOOL, 4)
      .put(ResourceType.LUMBER, 4)
      .put(ResourceType.GRAIN, 4)
      .put(ResourceType.BRICK, 3)
      .put(ResourceType.ORE, 3)
      .build();

  public static final Map<BuildingType, Stockpile> BUILDING_COSTS = ImmutableMap.<BuildingType, Stockpile> builder()
      .put(BuildingType.ROAD, Stockpile.of(ResourceType.LUMBER, ResourceType.BRICK))
      .put(BuildingType.SETTLEMENT, Stockpile.of(ResourceType.LUMBER, ResourceType.BRICK, ResourceType.WOOL,
          ResourceType.GRAIN))
      .put(BuildingType.CITY, Stockpile.of(ResourceType.ORE, ResourceType.ORE, ResourceType.ORE,
          ResourceType.GRAIN, ResourceType.GRAIN))
      .build();

  public static final Stockpile DEV_CARD_COST = Stockpile.of(ResourceType.WOOL, ResourceType.GRAIN, ResourceType.ORE);

  private GameConstants() {
  }
}
