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
   * It is a predicate for stopping work. @See {@link MetronomePredicate}
   * */
  public interface MetronomePredicate {
    /**
     * @param ctx context from around class
     * */
    boolean test(MetronomeGenerator ctx);
    static MetronomePredicate COUNT(int count){
    return ctx -> ctx.log().marker() < count;
    }
    static MetronomePredicate COUNT_SUCCESS(int count){
    return ctx -> ctx.log().success() < count;
    }
    static MetronomePredicate COUNT_FAILURE(int count){
    return ctx -> ctx.log().failure() < count;
    }
  }

  @Override
  Generator split() {
    return super.metronome(this.metronome,predicate);
  }
}
