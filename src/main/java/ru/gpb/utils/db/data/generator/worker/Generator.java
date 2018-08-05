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
public class Generator {
  private Logger LOGGER = Logger.getLogger(Generator.class.getName());
  private DatabaseEntityRelationsGenerator dbEntityRelationsGenerator;
  private DatabaseEntityGenerator dbEntityGenerator;
  protected InnerLog log;
  private DataGenerationException exception;


  Generator(DatabaseEntityRelationsGenerator multiEntityGenerator, DatabaseEntityGenerator singleEntityGenerator) {
    this.dbEntityRelationsGenerator = multiEntityGenerator;
    this.dbEntityGenerator = singleEntityGenerator;
    this.log = new InnerLog();
  }

  public String report(){
    return log.toString();
  }
  /**
   * Generate new instance for Class.
   * It tries to find this {@link MetaDataList#byClass(Class)}
   * Then it construct entity and relations.
   *
   * @param cl class for searching. Must have {@link javax.persistence.Entity} annotation.
   */
  public Generator generateByClass(Class<?> cl) {
    Optional<MetaData> pojoOpt = dbEntityRelationsGenerator.cache.metaDataList.byClass(cl);
    if (pojoOpt.isPresent()) {
      MetaData metaData = pojoOpt.get();
      try {
        process(metaData);
      } catch (DataGenerationException e) {
        LOGGER.info("generation by class " + cl.getSimpleName() + "has been failed - " + e.toString());
        exception = e;
        log.push(e.toString());
      }
    } else {
      DataGenerationException ex =
          new DataGenerationException("MetaData class " + cl.getName()
              + " is not found. Please check your configuration", new IllegalAccessException());
      LOGGER.info("generation by class " + cl.getSimpleName() + "has been failed - " + ex.toString());
      exception = ex;
      log.push(ex.toString());
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
  public Generator generateByTable(String schema, String table) {
    Optional<MetaData> pojoOpt = dbEntityRelationsGenerator.cache.metaDataList.bySchemaTable(schema, table);
    if (pojoOpt.isPresent()) {
      MetaData metaData = pojoOpt.get();
      try {
        process(metaData);
      } catch (DataGenerationException e) {
        LOGGER.info("generation by table " + schema + "." + table + "has been failed - " + e.toString());
        exception = e;
        log.push(e.toString());
      }
    } else {
      DataGenerationException ex = new DataGenerationException("MetaData class for table " + schema
          + "." + table + " is not found. Please check your configuration", new IllegalAccessException());
      LOGGER.info("generation by table " + schema + "." + table + "has been failed - " + ex.toString());
      exception = ex;
      log.push(ex.toString());

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
        log.push("entity generation for " + md.getHeader().toString());
      } catch (DataGenerationException e) {
        exception = e;
        log.push(e.toString());
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
        log.push("relations generation for " + metaData.getHeader().toString());
      } catch (DataGenerationException e) {
        exception = e;
        log.push(e.toString());
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
   * */
  public InnerCache cache(){
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
  public RepeatableGenerator repeate(int cycles) {
    return new RepeatableGenerator(dbEntityRelationsGenerator, dbEntityGenerator, cycles);
  }


  /**
   * Making new {@link MetronomeGenerator} for generating repeated events with special pauses.
   * Ihis method uses default implementation for Metronome @see {@link Metronome#systemParker(long, TimeUnit)}
   *
   * @param period metronome period
   * @param metric {@link TimeUnit} metric
   */
  public MetronomeGenerator metronome(long period, TimeUnit metric) {
    return new MetronomeGenerator(dbEntityRelationsGenerator, dbEntityGenerator, period, metric);
  }

  /**
   * Making new {@link MetronomeGenerator} for generating repeated events with special pauses.
   * Ihis method uses default implementation for Metronome @see {@link Metronome#systemParker(long, TimeUnit)}
   *
   * @param metronome custom metronome
   */
  public Generator metronome(Metronome metronome) {
    return new MetronomeGenerator(dbEntityRelationsGenerator, dbEntityGenerator, metronome);
  }

  public Generator startId(long val){
    this.dbEntityGenerator.setStartSeq(val);
    return this;
  }
  private void process(MetaData metaData) throws DataGenerationException {
    dbEntityGenerator.generateAndSaveObject(metaData);
    log.push("generate object: " + metaData.getHeader().toString());
    dbEntityRelationsGenerator.generateMultiObjects(metaData);
    log.push("generate relations: " + metaData.getHeader().toString());
  }


}
