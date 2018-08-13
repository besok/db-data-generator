package ru.gpb.utils.db.data.generator.worker;
// 2018.08.06 

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

// TODO: 8/13/2018 Доки!
// FIXME: 8/13/2018 Если запустить асинк на 1 таблицу 2 раза генерайтедбай, возникает гонка - меньше100
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
          close();
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
    for (Future<? extends Generator> genFuture : workers) {
      try {
        Generator gen = genFuture.get();
      } catch (InterruptedException | ExecutionException e) {
      }
    }
    close();
    return inner.cache();
  }

  @Override
  public InnerLog log() {
    InnerLog log = inner.log();
    for (Future<? extends Generator> genFuture : workers) {
      try {
        Generator gen = genFuture.get();
        log = new InnerLog(gen.log());
      } catch (InterruptedException | ExecutionException e) { }
    }
    close();
    return log;
  }

  @Override
  public String report() {
    StringBuilder sb = new StringBuilder();
    for (Future<? extends Generator> genFuture : workers) {
      try {
        sb.append(genFuture.get().report());
      } catch (InterruptedException | ExecutionException e) { }
    }
    close();
    return sb.toString();
  }

  @Override
  public Exception lastExeption() {
    for (Future<? extends Generator> genFuture : workers) {
      try {
        Generator gen = genFuture.get();
        if (Objects.nonNull(gen.lastExeption())) {
          return gen.lastExeption();
        }
      } catch (InterruptedException | ExecutionException e) {

      }
    }
    close();
    return inner.lastExeption();
  }

  @Override
  public Generator generateBy(Class<?> cl) {
    Generator splittedGenerator = this.inner.split();
    workers.add(executor.submit(() -> splittedGenerator.generateBy(cl)));
    return this;
  }

  @Override
  public Generator generateBy(String schema, String table) {
    Generator splittedGenerator = this.inner.split();
    Future<Generator> submittedGenerator = executor.submit(() -> splittedGenerator.generateBy(schema, table));
    workers.add(submittedGenerator);
    return this;
  }

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

  public void close(){
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
    return inner;
  }

  @Override
  Generator split() {
    throw new IllegalStateGeneratorException(" It can't possible invoking async for async generator.  ");
  }

}
