package models;

import java.util.Objects;


public class Resource {
  private final ResourceType _type;

  public Resource(ResourceType type) {
    _type = type;
  }

  public ResourceType getType() {
    return _type;
  }

  public static Resource ofType(ResourceType type) {
    return new Resource(type);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Resource)) {
      return false;
    }
    Resource that = (Resource) obj;
    return that._type.equals(_type);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(_type);
  }
}
