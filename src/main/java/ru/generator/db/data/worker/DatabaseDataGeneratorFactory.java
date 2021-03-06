package ru.generator.db.data.worker;
// 2018.07.24 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * api class for running plainValueGenerator
 *
 * @author Boris Zhguchev
 */

@Component
public class DatabaseDataGeneratorFactory {
  private DatabaseEntityRelationsGenerator multiEntityGenerator;
  private DatabaseEntityGenerator singleEntityGenerator;
  private Logger LOGGER = Logger.getLogger(DatabaseDataGeneratorFactory.class.getName());

  @Autowired
  public DatabaseDataGeneratorFactory(InnerCache cache, ApplicationContext context, MetaDataGraphBuilder builder) throws DataGenerationException {
    this.multiEntityGenerator = new DatabaseEntityRelationsGenerator(cache);
    this.singleEntityGenerator = new DatabaseEntityGenerator(context, cache);
    builder.buildRelation();
  }

  /**
   * dummyGenerator
   */
  public Generator dummyGenerator() {
    singleEntityGenerator.setGenerator(new DummyPlainTypeGenerator());
    multiEntityGenerator.cache.warm();
    LOGGER.info("dummyGenerator's been created");
    return new Generator(multiEntityGenerator, singleEntityGenerator);
  }

  /**
   * custom generator
   *
   * @param generator - custom plainTypeGenerator {@link PlainTypeGenerator}
   */

  public Generator generator(PlainTypeGenerator generator) {
    Objects.requireNonNull(generator);
    LOGGER.info("custom's been created. generator:" + generator.getClass().getName());
    singleEntityGenerator.setGenerator(generator);
    multiEntityGenerator.cache.warm();
    return new Generator(multiEntityGenerator, singleEntityGenerator);
  }

  /**
   * default generator
   *
   */

  public Generator generator() {

    LOGGER.info("default generator's been created");
    singleEntityGenerator.setGenerator(new ComplexPlainTypeGenerator());
    multiEntityGenerator.cache.warm();
    return new Generator(multiEntityGenerator, singleEntityGenerator);
  }

}
