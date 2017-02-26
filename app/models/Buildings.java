package models;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;


public class Buildings {
  private final Multiset<Building> buildings;

  public Buildings() {
    this(HashMultiset.create());
  }

  public Buildings(HashMultiset<Building> buildings) {
    this.buildings = buildings;
  }

  public void add(Building building, int count) {
    buildings.add(building, count);
  }

  public void remove(Building building) {
    buildings.remove(building, 1);
  }

  @Override
  public String toString() {
    return buildings.elementSet().stream()
        .map(building -> Pair.of(building, buildings.count(building)))
        .collect(Collectors.toList()).toString();
  }
}
