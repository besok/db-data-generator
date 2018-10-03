package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import ru.gpb.utils.db.data.generator.timer.Metronome;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Major class for processing generation request.
 * It has a fluent interface.
 *
 * @author Boris Zhguchev
 */
@SuppressWarnings("unchecked")
public class Generator {
  private Logger LOGGER = Logger.getLogger(Generator.class.getName());
  private DatabaseEntityRelationsGenerator dbEntityRelationsGenerator;
  private DatabaseEntityGenerator dbEntityGenerator;
  private DataGenerationException exception;
  protected InnerLog log;


  Generator(DatabaseEntityRelationsGenerator multiEntityGenerator, DatabaseEntityGenerator singleEntityGenerator) {
    this.dbEntityRelationsGenerator = multiEntityGenerator;
    this.dbEntityGenerator = singleEntityGenerator;
    this.log = new InnerLog("common generator");
  }

  /**
   * method adds rules for processing specific fields or columns or class
   * @param action Action changing or modifying old generated value. @see {@link Action}
   * @param predicate condition for action. @see {@link ColumnPredicate}
   * @param vClass value type. If it is different with field it will do nothing, in order to it is additional filter
   * @return this
   * */
  public<V> Generator rule(ColumnPredicate predicate, Action<V> action,Class<V> vClass){
    dbEntityGenerator.setPair(predicate,action,vClass);
    return this;
  }
  /**
   * method adds rules for processing id field
   * @param action Action changing or modifying old generated value. @see {@link Action}
   * @param pojo condition for action. @see {@link ColumnPredicate}
   * @return this
   * */
  public<V> Generator ruleId(Class<?> pojo, Action<V> action){
    dbEntityGenerator.setPairForId(pojo,action);
    return this;
  }

  /**
   *
   * @return report @see {@link InnerLog#toString()}
   *
   * */
  public String report() {
    return log.toString();
  }

  /**
   * Generate new instance for Class.
   * It tries to find this {@link MetaDataList#byClass(Class)}
   * Then it construct entity and relations.
   *
   * @param cl class for searching. Must have {@link javax.persistence.Entity} annotation.
   */
  public Generator generateBy(Class<?> cl) {
    Optional<MetaData> pojoOpt = dbEntityRelationsGenerator.cache.metaDataList.byClass(cl);
    if (pojoOpt.isPresent()) {
      MetaData metaData = pojoOpt.get();
      try {
        process(metaData);
      } catch (DataGenerationException e) {
        LOGGER.finest("generation by class " + cl.getSimpleName() + "has been failed - " + e.toString());
        log.failureInc();
        this.exception=e;
      }
    } else {
      DataGenerationException ex =
          new DataGenerationException("MetaData class " + cl.getName()
              + " is not found. Please check your configuration", new IllegalAccessException());
      LOGGER.finest("generation by class " + cl.getSimpleName() + "has been failed - " + ex.toString());
      log.failureInc();
      this.exception=ex;
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
  public Generator generateBy(String schema, String table) {
    Optional<MetaData> pojoOpt = dbEntityRelationsGenerator.cache.metaDataList.bySchemaTable(schema, table);
    if (pojoOpt.isPresent()) {
      MetaData metaData = pojoOpt.get();
      try {
        process(metaData);
      } catch (DataGenerationException e) {
        LOGGER.finest("generation by table " + schema + "." + table + "has been failed - " + e.toString());
        log.failureInc();
        this.exception=e;
      }
    } else {
      DataGenerationException ex = new DataGenerationException("MetaData class for table " + schema
          + "." + table + " is not found. Please check your configuration", new IllegalAccessException());
      LOGGER.finest("generation by table " + schema + "." + table + "has been failed - " + ex.toString());
      log.failureInc();
      this.exception=ex;

    }
    return this;
  }


  /**
   * Generate new instance and relations for all founded Class
   **/
  public Generator generateAll() {
    return generateObjects().generateRelations();
  }


  /**
   * generate all instances except m2m relations.
   */
  public Generator generateObjects() {
    Set<MetaData> metaData = dbEntityGenerator.cache.metas();

    for (MetaData md : metaData) {
      try {
        dbEntityGenerator.generateAndSaveObject(md);
        log.successInc();
      } catch (DataGenerationException e) {
        log.failureInc();
        this.exception=e;
      }
    }

    return this;
  }

  /**
   * generate all relations except generating instances.
   * They can be found in the cache {@link InnerCache}
   */
  public Generator generateRelations() {
    for (MetaData metaData : dbEntityRelationsGenerator.cache.metas()) {
      try {
        dbEntityRelationsGenerator.generateMultiObjects(metaData);
        log.successInc();
      } catch (DataGenerationException e) {
        log.failureInc();
        this.exception=e;
      }
    }
    return this;
  }


  /**
   * throw on next stage saved exception.
   *
   * @throws DataGenerationException for processing it on next stage
   */

  public Generator withException() throws DataGenerationException {
    if (Objects.nonNull(exception))
      throw exception;

    return this;
  }

  /**
   * inner cache {@link InnerCache}
   */
  public InnerCache cache() {
    return dbEntityRelationsGenerator.getCache();
  }

  /**
   * returning log entity @see {@link InnerLog}
   */
  public InnerLog log() {
    return new InnerLog(log);
  }

  /**
   * returning last cached exception
   */
  public Exception lastExeption() {
    return exception;
  }

  /**
   * Making new {@link RepeatableGenerator} for generating repeated events
   *
   * @param cycles - cycles for repeate
   */
  public Generator repeate(int cycles) {
    return new RepeatableGenerator(dbEntityRelationsGenerator, dbEntityGenerator, cycles);
  }


  /**
   * Making new {@link MetronomeGenerator} for generating repeated events with special pauses.
   * Ihis method uses default implementation for Metronome @see {@link Metronome#systemParker(long, TimeUnit)}
   *
   * @param period metronome period
   * @param metric {@link TimeUnit} metric
   */
  public Generator metronome(long period, TimeUnit metric) {
    return new MetronomeGenerator(dbEntityRelationsGenerator, dbEntityGenerator, period, metric, ctx -> true);
  }
  /**
   * Making new {@link MetronomeGenerator} for generating repeated events with special pauses.
   * Ihis method uses default implementation for Metronome @see {@link Metronome#systemParker(long, TimeUnit)}
   *
   * @param metronome @see {@link Metronome}
   * @param predicate condition for stopping generator @see {@link ru.gpb.utils.db.data.generator.worker.MetronomePredicate}
   */
  public Generator metronome(Metronome metronome, MetronomePredicate predicate) {
    return new MetronomeGenerator(dbEntityRelationsGenerator, dbEntityGenerator, metronome, predicate);
  }

  /**
   * Making new {@link MetronomeGenerator} for generating repeated events with special pauses.
   * Ihis method uses default implementation for Metronome @see {@link Metronome#systemParker(long, TimeUnit)}
   *
   * @param period metronome period
   * @param metric {@link TimeUnit} metric
   */
  public Generator metronome(long period, TimeUnit metric, MetronomePredicate predicate) {
    return new MetronomeGenerator(dbEntityRelationsGenerator, dbEntityGenerator, period, metric, predicate);
  }


  /**
   * set startId for default id generator(incrementing from 0 by default).
   *
   * If annotation Id has no annotation GeneratedValue a generator will be generate id from sequence
   * by incrementing numbers or making random values from UUID or String
   *
   * @param val initial value for generating id.
   *
   * */
  public Generator startId(long val) {
    this.dbEntityGenerator.setStartSeq(val);
    return this;
  }

  /**
   * Making new {@link AsyncGenerator} for generating events each in separate thread .
   */
  public Generator async() {
    return new AsyncGenerator(dbEntityRelationsGenerator, dbEntityGenerator, this);
  }

  public Generator async(int nThreads) {
    return new AsyncGenerator(dbEntityRelationsGenerator, dbEntityGenerator, this, nThreads);
  }

  private void process(MetaData metaData) throws DataGenerationException {
    dbEntityGenerator.generateAndSaveObject(metaData);
    dbEntityRelationsGenerator.generateMultiObjects(metaData);
    log.successInc();
  }

  /**
   * Terminal finish operation.
   *
   * @return this
   */
  public Generator finish() {
    return this;
  }

  Generator split() {
    return new Generator(this.dbEntityRelationsGenerator, this.dbEntityGenerator);
  }
}
