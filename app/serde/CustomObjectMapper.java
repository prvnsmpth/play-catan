package serde;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import play.libs.Json;


public class CustomObjectMapper {
  public CustomObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    mapper.registerModule(new GuavaModule());
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    mapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
    mapper.setFilterProvider(new SimpleFilterProvider().addFilter("filter", SimpleBeanPropertyFilter.serializeAll()));
    Json.setObjectMapper(mapper);
  }
}
