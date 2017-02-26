package modules;

import com.google.inject.AbstractModule;
import serde.CustomObjectMapper;


public class CustomObjectMapperModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(CustomObjectMapper.class).asEagerSingleton();
  }
}
