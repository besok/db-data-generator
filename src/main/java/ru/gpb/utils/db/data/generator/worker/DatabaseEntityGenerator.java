package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author Boris Zhguchev
 */
@SuppressWarnings("unchecked")
public class DatabaseEntityGenerator {

  private Logger LOGGER = Logger.getLogger(DatabaseDataGeneratorFactory.class.getName());

  private PlainTypeGenerator plainValueGenerator;
  private IdSeqGenerator seqGen;

  public void setGenerator(PlainTypeGenerator generatorSupplier) {
    LOGGER.info("generator's been changed = " + generatorSupplier.getClass().getName());
    this.plainValueGenerator = generatorSupplier;
  }

  private Repositories repositories;
  protected final InnerCache cache;


  DatabaseEntityGenerator(ApplicationContext context, InnerCache cache) {
    this.seqGen = new IdSeqGenerator(0);
    this.repositories = new Repositories(context);
    this.cache = cache;
  }

  public void setStartSeq(long startSeq) {
    seqGen = new IdSeqGenerator(startSeq);
  }

  protected Optional<Object> generateAndSaveSimpleObject(MetaData metaData) throws DataGenerationException {
    Class<?> aClass = metaData.getAClass();
    Object ent = null;
    try {
      ent = aClass.newInstance();

      MetaData.Id id = metaData.getId();
      if (!id.isGenerated()) {
        Field idField = id.getIdField();
        idField.setAccessible(true);
        Object generatedId = seqGen.generate(idField.getType(), null);
        idField.set(ent, generatedId);
      }

      for (MetaData.Column col : metaData.getPlainColumns()) {
        Field f = col.getVal();
        f.setAccessible(true);
        Object generatedObject = plainValueGenerator.generate(f.getType(), col);
        f.set(ent, generatedObject);
      }
    } catch (InstantiationException | IllegalAccessException e) {
      LOGGER.info("exception's been caught: " + e.getClass().getSimpleName() + " for " + metaData.getAClass().getSimpleName());
      throw new DataGenerationException("Reflection exception", e);
    }

    return save(aClass, ent).map(cache(metaData));
  }

  protected Optional<Object> generateAndSaveObject(MetaData metaData) throws DataGenerationException {
    if (metaData.isPlain())
      return generateAndSaveSimpleObject(metaData);

    Class<?> aClass = metaData.getAClass();
    Object obj = null;
    try {
      obj = aClass.newInstance();

      for (Field f : aClass.getDeclaredFields()) {
        f.setAccessible(true);
        if (metaData.isId(f)) {
          if (!metaData.getId().isGenerated()) {
            MetaData.Id id = metaData.getId();
            Field idField = id.getIdField();
            idField.setAccessible(true);
            Object generatedId = seqGen.generate(idField.getType(), null);
            idField.set(obj, generatedId);
          }
        } else {
          MetaData before = metaData.dependency(f);
          if (before == null) {
            Class<?> type = f.getType();
            Optional<MetaData.Column> column = metaData.findByField(f);
            if (column.isPresent()) {
              // TODO: 8/2/2018 Сделать обработку если это коллекция
              Object generate = plainValueGenerator.generate(type, column.get());
              f.set(obj, generate);
            } else {
              if (metaData.neighbour(f) == null) {
                throw new InstantiationException();
              }
            }
          } else {
            Optional<Object> beforePojo = generateAndSaveObject(before);
            if (beforePojo.isPresent())
              f.set(obj, beforePojo.get());
          }
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


  private class IdSeqGenerator extends AbstractPlainTypeGenerator {


    private AtomicLong seq;

    public IdSeqGenerator(long seqStart) {
      this.seq = new AtomicLong(seqStart);
    }

    @Override
    public Function<MetaData.Column, UUID> uuid() {
      return unpack(UUID.randomUUID());
    }


    @Override
    public Function<MetaData.Column, BigDecimal> bigDecimal() {
      return unpack(BigDecimal.valueOf(seq.incrementAndGet()));
    }

    @Override
    public Function<MetaData.Column, Integer> integer() {
      return unpack(Math.toIntExact(seq.incrementAndGet()));
    }

    @Override
    public Function<MetaData.Column, Double> doubleVal() {
      return unpack((double) seq.incrementAndGet());
    }

    @Override
    public Function<MetaData.Column, Long> longV() {
      return unpack(seq.incrementAndGet());
    }
  }

}
