package ru.gpb.utils.db.data.generator.worker;
// 2018.07.25 

import ru.gpb.utils.db.data.generator.timer.Metronome;

import java.util.concurrent.TimeUnit;

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



  private Metronome metronome;

  private MetronomePredicate predicate = ctx -> true;


  @Override
  public Generator generateBy(Class<?> cl) {

    while (predicate.test(this)) {
      try {
        metronome.pause();
        super.generateBy(cl);
      } catch (InterruptedException e) {
        log.push(e.getClass() + ".");
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
        log.push(e.getClass() + ".");
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
        log.push(e.getClass() + ".");
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
        log.push(e.getClass() + ".");
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
        log.push(e.getClass() + ".");
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
    return ctx -> ctx.log().markerValue() < count;
    }
  }

  @Override
  Generator split() {
    return super.metronome(this.metronome,predicate);
  }
}
