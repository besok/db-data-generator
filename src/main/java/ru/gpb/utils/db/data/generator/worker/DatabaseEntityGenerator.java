package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author Boris Zhguchev
 */
@SuppressWarnings("unchecked")
public class DatabaseEntityGenerator {

  private Logger LOGGER = Logger.getLogger(DatabaseDataGeneratorFactory.class.getName());

  private PlainTypeGeneratorSupplier plainValueGenerator;

  public void setGenerator(PlainTypeGeneratorSupplier generatorSupplier) {
    LOGGER.info("generator's been changed = " + generatorSupplier.getClass().getName());
    this.plainValueGenerator = generatorSupplier;
  }

  private Repositories repositories;
  protected final InnerCache cache;


  public DatabaseEntityGenerator(ApplicationContext context, InnerCache cache) {
    this.repositories = new Repositories(context);
    this.cache = cache;
  }


  protected Optional<Object> generateSimpleObject(MetaData metaData) throws DataGenerationException {
    Class<?> aClass = metaData.getAClass();
    Object ent = null;
    try {
      ent = aClass.newInstance();
      for (Field f : aClass.getDeclaredFields()) {
        f.setAccessible(true);
        Class<?> type = f.getType();
        Optional<MetaData.Column> column = metaData.findByField(f);
        if (column.isPresent()) {
          Object generate = plainValueGenerator.generate(type, column.get());
          f.set(ent, generate);
        }
        else {
          throw new InstantiationException();
        }
      }
    } catch (InstantiationException | IllegalAccessException e) {
      LOGGER.info("exception's been caught: " + e.getClass().getSimpleName() + " for " + metaData.getAClass().getSimpleName());
      throw new DataGenerationException("Reflection exception", e);
    }

    return save(aClass, ent).map(cache(metaData));
  }

  protected Optional<Object> generateObject(MetaData metaData) throws DataGenerationException {
    if (metaData.isPlain())
      return generateSimpleObject(metaData);

    Class<?> aClass = metaData.getAClass();
    Object obj = null;
    try {
      obj = aClass.newInstance();

      for (Field f : aClass.getDeclaredFields()) {
        f.setAccessible(true);
        MetaData before = metaData.dependency(f);
        if (before == null) {
          Class<?> type = f.getType();
          Optional<MetaData.Column> column = metaData.findByField(f);
          if (column.isPresent()) {
            // TODO: 8/2/2018 Сделать обработку если это коллекция
            Object generate = plainValueGenerator.generate(type, column.get());
            f.set(obj, generate);
          }
          else {
              throw new InstantiationException();
          }
        } else {
          Optional<Object> beforePojo = generateObject(before);
          if (beforePojo.isPresent())
            f.set(obj, beforePojo.get());
        }
      }
    } catch (InstantiationException | IllegalAccessException e) {
      LOGGER.info("exception's been caught: " + e.getClass().getSimpleName() + " for " + metaData.getAClass().getSimpleName());
      throw new DataGenerationException("Reflection exception ", e);
    }
    return save(aClass, obj).map(cache(metaData));

  }

  private Function<Object, Object> cache(MetaData metaData) {
    return c -> {
      cache.put(metaData, c);
      return c;
    };
  }

  private Optional<Object> save(Class<?> aClass, Object ent) {
    return repositories.getRepositoryFor(aClass).map(o -> ((JpaRepository) o).save(ent));
  }



}
