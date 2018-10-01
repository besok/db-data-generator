package ru.gpb.utils.db.data.generator.worker;
// 2018.07.25 

import ru.gpb.utils.db.data.generator.timer.Metronome;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 *
 * Generator for generation events with special pauses
 *
 * @author Boris Zhguchev
 */
public class MetronomeGenerator extends Generator {
  MetronomeGenerator(DatabaseEntityRelationsGenerator multiEntityGenerator, DatabaseEntityGenerator singleEntityGenerator,
                     long period, TimeUnit metric,
                     MetronomePredicate predicate) {
    super(multiEntityGenerator, singleEntityGenerator);
    this.metronome = Metronome.systemParker(period, metric);
    this.predicate=predicate;
  }

  public MetronomeGenerator(DatabaseEntityRelationsGenerator multiEntityGenerator,
                            DatabaseEntityGenerator singleEntityGenerator,
                            Metronome metronome, MetronomePredicate predicate) {

    super(multiEntityGenerator, singleEntityGenerator);
    this.metronome = metronome;
    this.predicate = predicate;
  }

  MetronomeGenerator(DatabaseEntityRelationsGenerator multiEntityGenerator, DatabaseEntityGenerator singleEntityGenerator,
                     Metronome metronome) {
    super(multiEntityGenerator, singleEntityGenerator);
    this.metronome = metronome;
  }


  private Logger LOGGER = Logger.getLogger(MetronomeGenerator.class.getName());

  private Metronome metronome;
  private MetronomePredicate predicate = ctx -> true;

  /**
   * Generate new instance for Class.
   * It tries to find this {@link MetaDataList#byClass(Class)}
   * Then it construct entity and relations.
   *
   * @param cl class for searching. Must have {@link javax.persistence.Entity} annotation.
   */
  @Override
  public Generator generateBy(Class<?> cl) {

    while (predicate.test(this)) {
      try {
        metronome.pause();
        super.generateBy(cl);
      } catch (InterruptedException e) {
        log.failureInc();
        LOGGER.info("generation by class " + cl.getSimpleName() + "has been failed - " + e.toString());
      }
    }
    return this;
  }

  /**
   * Generate new instance for Class based on table name(schema.table)
   * It tries to find this {@link MetaDataList#bySchemaTable(String, String)}}
   * Then it construct entity and relations.
   *
   * @param schema sch for searching. Must be present in {@link javax.persistence.Table} annotation.
   * @param table  sch for searching. Must be present in {@link javax.persistence.Table} annotation.
   */
  @Override
  public Generator generateBy(String schema, String table) {
    while (predicate.test(this)) {
      try {
        metronome.pause();
        super.generateBy(schema, table);
      } catch (InterruptedException e) {
        log.failureInc();
        LOGGER.info("generation by class " + schema+"."+table + "has been failed - " + e.toString());
      }
    }
    return this;
  }

  /**
   * Generate new instance and relations for all founded Class
   **/
  @Override
  public Generator generateAll() {
    while (predicate.test(this)) {
      try {
        metronome.pause();
        super.generateAll();
      } catch (InterruptedException e) {
        log.failureInc();
        LOGGER.info("generation has been failed - " + e.toString());
      }
    }
    return this;
  }

  /**
   * generate all instances except m2m relations.
   */
  @Override
  public Generator generateObjects() {
    while (predicate.test(this)) {
      try {
        metronome.pause();
        super.generateObjects();
      } catch (InterruptedException e) {
        log.failureInc();
        LOGGER.info("generation has been failed - " + e.toString());
      }
    }
    return this;
  }

  /**
   * generate all relations except generating instances.
   * They can be found in the cache {@link InnerCache}
   */
  @Override
  public Generator generateRelations() {
    while (predicate.test(this)) {
      try {
        metronome.pause();
        super.generateRelations();
      } catch (InterruptedException e) {
        log.failureInc();
        LOGGER.info("generation has been failed - " + e.toString());
      }
    }
    return this;

  }
  /**
   * method adds rules for processing specific fields or columns or class
   * @param action Action changing or modifying old generated value. @see {@link Action}
   * @param predicate condition for action. @see {@link ColumnPredicate}
   * @param vClass value type. If it is different with field it will do nothing, in order to it is additional filter
   * @return this
   *
   * */
  @Override
  public <V> Generator rule(ColumnPredicate predicate, Action<V> action, Class<V> vClass) {
    super.rule(predicate, action, vClass);
    return this;
  }

  @Override
  Generator split() {
    return super.metronome(this.metronome,predicate);
  }
}
