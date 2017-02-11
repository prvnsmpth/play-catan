package models;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.Collection;
import java.util.Collections;


public class Buildings {
  private final Multiset<Building> _buildings;

  public Buildings() {
    this(Collections.EMPTY_SET);
  }

  public Buildings(Collection<Building> buildings) {
    _buildings = HashMultiset.create();
    _buildings.addAll(buildings);
  }

  public void add(Building building, int count) {
    _buildings.add(building, count);
  }
}
