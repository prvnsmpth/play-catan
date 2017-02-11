package models;

import com.google.common.collect.HashMultiset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Represents a collection of resources.
 */
public class Stockpile implements Iterable<Resource> {
  private final HashMultiset<Resource> _supplies;

  public Stockpile() {
    this(Collections.EMPTY_LIST);
  }

  public static Stockpile of(ResourceType... resourceTypes) {
    List<Resource> resources = Arrays.stream(resourceTypes).map(Resource::ofType).collect(Collectors.toList());
    return new Stockpile(resources);
  }

  public Stockpile(Collection<Resource> resources) {
    _supplies = HashMultiset.create();
    _supplies.addAll(resources);
  }

  public void add(Resource type, int count) {
    _supplies.add(type, count);
  }

  public int getCount(Resource resource) {
    return _supplies.count(resource);
  }

  public void remove(Stockpile pile) {
    for (Resource resource : pile) {
      if (getCount(resource) < pile.getCount(resource)) {
        throw new IllegalStateException("Not enough resources in pile to remove");
      }
      _supplies.remove(resource, pile.getCount(resource));
    }
  }

  public void add(Stockpile pile) {
    for (Resource resource : pile) {
      _supplies.add(resource, pile.getCount(resource));
    }
  }

  @Override
  public Iterator<Resource> iterator() {
    return _supplies.elementSet().iterator();
  }
}
