package ru.gpb.utils.db.data.generator.worker;
// 2018.08.06 

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;


/**
 * @author Boris Zhguchev
 */
// FIXME: 8/6/2018
public class AsyncGenerator extends Generator {
  AsyncGenerator(DatabaseEntityRelationsGenerator multiEntityGenerator,
                 DatabaseEntityGenerator singleEntityGenerator,
                 Generator initGen) {
    super(multiEntityGenerator, singleEntityGenerator);
    this.inner = initGen;
    this.executor = Executors.newFixedThreadPool(10);
    this.workers = new ArrayList<>();
  }

  AsyncGenerator(DatabaseEntityRelationsGenerator multiEntityGenerator,
                 DatabaseEntityGenerator singleEntityGenerator,
                 Generator initGen, int nThreads) {
    super(multiEntityGenerator, singleEntityGenerator);
    this.inner = initGen;
    this.executor = Executors.newFixedThreadPool(nThreads);
    this.workers = new ArrayList<>();
  }

  private Generator inner;
  private List<Future<? extends Generator>> workers;
  private ExecutorService executor;

  @Override
  public Generator withException() throws DataGenerationException {
    for (Future<? extends Generator> genFuture : workers) {
      try {
        Generator gen = genFuture.get();
        if (Objects.nonNull(gen.lastExeption())) {
          return gen.withException();
        }
      } catch (InterruptedException | ExecutionException e) {
        throw new DataGenerationException(e);
      }
    }
    return inner.withException();
  }

  @Override
  public InnerCache cache() {
    InnerCache cache = inner.cache();
    for (Future<? extends Generator> genFuture : workers) {
      try {
        Generator gen = genFuture.get();
        cache=InnerCache.concat(cache, gen.cache());
      } catch (InterruptedException | ExecutionException e) {}
    }
    return inner.cache();
  }

  @Override
  public InnerLog log() {
    return inner.log();
  }

  @Override
  public String report() {
    return inner.report();
  }

  @Override
  public Exception lastExeption() {
    return inner.lastExeption();
  }

  @Override
  public Generator generateBy(Class<?> cl) {
    Generator splittedGenerator = this.inner.split();
    Future<Generator> submittedGenerator = executor.submit(() -> splittedGenerator.generateBy(cl));
    workers.add(submittedGenerator);
    return this;
  }

  @Override
  public Generator generateBy(String schema, String table) {
    Generator splittedGenerator = this.inner.split();
    Future<Generator> submittedGenerator = executor.submit(() -> splittedGenerator.generateBy(schema, table));
    workers.add(submittedGenerator);
    return this;
  }


  // TODO: 8/13/2018 добавить параллелизм
  @Override
  public Generator generateAll() {
    return inner.generateAll();
  }

  @Override
  public Generator generateObjects() {
    return inner.generateObjects();
  }

  @Override
  public Generator generateRelations() {
    return inner.generateRelations();
  }

  @Override
  public Generator repeate(int cycles) {
    this.inner = super.repeate(cycles);
    return this;
  }

  @Override
  public Generator metronome(long period, TimeUnit metric) {
    this.inner = super.metronome(period, metric);
    return this;
  }

  @Override
  public Generator metronome(long period, TimeUnit metric, MetronomeGenerator.MetronomePredicate predicate) {
    this.inner = super.metronome(period, metric, predicate);
    return this;
  }

  @Override
  public Generator startId(long val) {
    return inner.startId(val);
  }


  @Override
  public Generator async() {
    return this;
  }

  @Override
  public Generator async(int nThreads) {
    return this;
  }

  @Override
  Generator split() {
    throw new IllegalStateGeneratorException(" It can't possible invoking async for async generator.  ");
  }

}
