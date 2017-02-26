package models;

import com.google.common.collect.HashMultiset;
import exceptions.NotEnoughResourcesException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import models.constants.ResourceType;


/**
 * Represents a collection of resources.
 */
public class Stockpile implements Iterable<Resource> {
  private final HashMultiset<Resource> supplies;

  public Stockpile() {
    this(HashMultiset.create());
  }

  public static Stockpile of(ResourceType... resourceTypes) {
    List<Resource> resources = Arrays.stream(resourceTypes).map(Resource::ofType).collect(Collectors.toList());
    return new Stockpile(HashMultiset.create(resources));
  }

  public static Stockpile of(ResourceType resourceType, int count) {
    List<Resource> resources = IntStream.range(0, count)
        .mapToObj(i -> new Resource(resourceType)).collect(Collectors.toList());
    return new Stockpile(HashMultiset.create(resources));
  }

  public Stockpile(HashMultiset<Resource> supplies) {
    this.supplies = supplies;
  }

  public void add(Resource type, int count) {
    supplies.add(type, count);
  }

  public int getCount(Resource resource) {
    return supplies.count(resource);
  }

  public void remove(Stockpile pile) throws NotEnoughResourcesException {
    for (Resource resource : pile) {
      if (getCount(resource) < pile.getCount(resource)) {
        throw new NotEnoughResourcesException(this, pile);
      }
      supplies.remove(resource, pile.getCount(resource));
    }
  }

  public Stockpile add(Stockpile pile) {
    for (Resource resource : pile) {
      supplies.add(resource, pile.getCount(resource));
    }
    return this;
  }

  @Override
  public Iterator<Resource> iterator() {
    return supplies.elementSet().iterator();
  }
}
