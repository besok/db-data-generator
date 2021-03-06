package ru.generator.db.data.worker;
// 2018.08.06 


import ru.generator.db.data.timer.Metronome;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.logging.Logger;


/**
 * @author Boris Zhguchev
 *
 * Class for processing data in different threads.
 * it needed terminal operation @see {@link AsyncGenerator#log()}
 * or @see {@link AsyncGenerator#report()}
 * or @see {@link AsyncGenerator#finish()}
 *
 */
public class AsyncGenerator extends Generator {
  AsyncGenerator(DatabaseEntityRelationsGenerator multiEntityGenerator,
                 DatabaseEntityGenerator singleEntityGenerator,
                 Generator initGen) {
    super(multiEntityGenerator, singleEntityGenerator);
    this.delegate = initGen;
    this.executor = Executors.newFixedThreadPool(10);
    this.workers = new ArrayList<>();
  }

  AsyncGenerator(DatabaseEntityRelationsGenerator multiEntityGenerator,
                 DatabaseEntityGenerator singleEntityGenerator,
                 Generator initGen, int nThreads) {
    super(multiEntityGenerator, singleEntityGenerator);
    this.delegate = initGen;
    this.executor = Executors.newFixedThreadPool(nThreads);
    this.workers = new ArrayList<>();
  }

  private Logger LOGGER = Logger.getLogger(AsyncGenerator.class.getName());

  private Generator delegate;
  private List<Future<? extends Generator>> workers;
  private ExecutorService executor;


  /**
   * throw on next stage saved exception.
   *
   * @throws DataGenerationException for processing it on next stage
   */

  @Override
  public Generator withException() throws DataGenerationException {
    for (Future<? extends Generator> genFuture : workers) {
      try {
        Generator gen = genFuture.get();
        if (Objects.nonNull(gen.lastExeption())) {
          close();
          return gen.withException();
        }
      } catch (InterruptedException | ExecutionException e) {
        throw new DataGenerationException(e);
      }
    }

    return this;
  }

  /**
   *
   * @return InnerCache singleton;
   *
   * */

  @Override
  public InnerCache cache() {
    for (Future<? extends Generator> genFuture : workers) {
      try {
        Generator gen = genFuture.get();
      } catch (InterruptedException | ExecutionException e) {
        LOGGER.finest("while calculate cache =>  exception +"+e.toString());
      }
    }
    close();
    return delegate.cache();
  }



  /**
   *
   * @return InnerLog instance;
   *
   * */
  @Override
  public InnerLog log() {
    InnerLog log = delegate.log();
    for (Future<? extends Generator> genFuture : workers) {
      try {
        Generator gen = genFuture.get();
        log = new InnerLog(gen.log());
      } catch (InterruptedException | ExecutionException e) {
        LOGGER.finest("while calculate log =>  exception +"+e.toString());
      }
    }
    close();
    return log;
  }


  /**
   *
   * @return report @see {@link InnerLog#toString()};
   *
   * */
  @Override
  public String report() {
    StringBuilder sb = new StringBuilder();
    for (Future<? extends Generator> genFuture : workers) {
      try {
        sb.append(genFuture.get().report());
        sb.append(System.lineSeparator());
      } catch (InterruptedException | ExecutionException e) {
        LOGGER.finest("while calculate report from log =>  exception +"+e.toString());
      }
    }
    close();
    return sb.toString();
  }

  /**
   *
   * @return last exception from first command;
   *
   * */
  @Override
  public Exception lastExeption() {
    for (Future<? extends Generator> genFuture : workers) {
      try {
        Generator gen = genFuture.get();
        if (Objects.nonNull(gen.lastExeption())) {
          return gen.lastExeption();
        }
      } catch (InterruptedException | ExecutionException e) {
        LOGGER.finest("while calculate last exeption =>  exception +"+e.toString());
      }
    }
    close();
    return delegate.lastExeption();
  }

  /**
   * Generate new instance for Class.
   * It tries to find this {@link MetaDataList#byClass(Class)}
   * Then it construct entity and relations.
   *
   * @param cl class for searching. Must have {@link javax.persistence.Entity} annotation.
   */
  @Override
  public Generator generateBy(Class<?> cl) {
    LOGGER.fine("generated by "+ cl.getName());
    Generator splittedGenerator = this.delegate.split();
    workers.add(executor.submit(() -> splittedGenerator.generateBy(cl)));
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
    LOGGER.finest("generated by "+ schema+"."+table);

    Generator splittedGenerator = this.delegate.split();
    Future<Generator> submittedGenerator = executor.submit(() -> splittedGenerator.generateBy(schema, table));
    workers.add(submittedGenerator);
    return this;
  }

  /**
   * Generate new instance and relations for all founded Class
   **/
  @Override
  public Generator generateAll() {
    LOGGER.finest("generated all");
    return delegate.generateAll();
  }


  /**
   * generate all instances except m2m relations.
   */
  @Override
  public Generator generateObjects() {
    LOGGER.finest("generated all objects");

    return delegate.generateObjects();
  }

  /**
   * generate all relations except generating instances.
   * They can be found in the cache {@link InnerCache}
   */
  @Override
  public Generator generateRelations() {
    LOGGER.finest("generated all relations");
    return delegate.generateRelations();
  }

  /**
   * Making new {@link RepeatableGenerator} for generating repeated events
   *
   * @param cycles - cycles for repeate
   */
  @Override
  public Generator repeate(int cycles) {
    this.delegate = super.repeate(cycles);
    LOGGER.finest("repeate "+cycles+" times");
    return this;
  }

  /**
   * Making new {@link MetronomeGenerator} for generating repeated events with special pauses.
   * Ihis method uses default implementation for Metronome @see {@link Metronome#systemParker(long, TimeUnit)}
   *
   * @param period metronome period
   * @param metric {@link TimeUnit} metric
   */
  @Override
  public Generator metronome(long period, TimeUnit metric) {
    this.delegate = super.metronome(period, metric);
    LOGGER.finest("metronome "+ period + " "+metric.name());
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
    delegate.rule(predicate, action, vClass);
    return this;
  }

  /**
   * method adds rules for processing id field
   * @param action Action changing or modifying old generated value. @see {@link Action}
   * @param pojo condition for action. @see {@link ColumnPredicate}
   * @return this
   * */
  @Override
  public<V> Generator ruleId(Class<?> pojo, Action<V> action){
    delegate.ruleId(pojo,action);
    return this;
  }

  /**
   * Making new {@link MetronomeGenerator} for generating repeated events with special pauses.
   * Ihis method uses default implementation for Metronome @see {@link Metronome#systemParker(long, TimeUnit)}
   *
   * @param period metronome period
   * @param metric {@link TimeUnit} metric
   * @param predicate condition for stopping generator @see {@link MetronomePredicate}
   */
  @Override
  public Generator metronome(long period, TimeUnit metric, MetronomePredicate predicate) {
    this.delegate = super.metronome(period, metric, predicate);
    LOGGER.finest("metronome "+ period + " "+metric.name() +" with predicate ");
    return this;
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
  @Override
  public Generator startId(long val) {
    LOGGER.finest("set start id = "+val);
    return delegate.startId(val);
  }


  /**
   * Making new {@link AsyncGenerator} for generating repeated events with special pauses.
   * @return this
   *
   */
  @Override
  public Generator async() {
    return this;
  }

  /**
   * Making new {@link AsyncGenerator} for generating repeated events with special pauses.
   * @param nThreads - thread count for using by  generator
   * @return this
   *
   */
  @Override
  public Generator async(int nThreads) {
    return this;
  }


  private void close(){
    executor.shutdown();
    try {
      if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }
  }

  /**
   * terminal operation. It waits all futures completed.
   *
   * @return this
   * */
  @Override
  public Generator finish(){
    for (Future<? extends Generator> worker : workers) {
      try {
        worker.get();
      } catch (InterruptedException | ExecutionException e) {}
    }
    close();
    return delegate;
  }

  @Override
  Generator split() {
    throw new IllegalStateGeneratorException(" It can't possible invoking async for async generator.  ");
  }



}
