package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Objects;
import models.constants.ResourceType;


public class Resource {

  private final ResourceType type;

  public Resource(ResourceType type) {
    this.type = type;
  }

  @JsonCreator
  public Resource(String type) {
    this.type = ResourceType.fromValue(type);
  }

  public ResourceType getType() {
    return type;
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
    return that.type.equals(type);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type);
  }
}
