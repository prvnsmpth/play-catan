package models.constants;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import models.BuildingType;
import models.ResourceType;
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

  public static final Stockpile ROAD_COST = Stockpile.of(ResourceType.LUMBER, ResourceType.BRICK);

  public static final Stockpile SETTLEMENT_COST = Stockpile.of(ResourceType.LUMBER, ResourceType.BRICK,
      ResourceType.WOOL, ResourceType.GRAIN);

  public static final Stockpile CITY_COST = Stockpile.of(ResourceType.ORE, ResourceType.ORE, ResourceType.ORE,
      ResourceType.GRAIN, ResourceType.GRAIN);

  private GameConstants() {
  }
}
