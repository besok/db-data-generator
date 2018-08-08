package ru.gpb.utils.db.data.generator.worker;
// 2018.08.06 

import java.util.concurrent.TimeUnit;

/**
 * @author Boris Zhguchev
 */
// FIXME: 8/6/2018
public class ParallelGenerator extends Generator {
  ParallelGenerator(DatabaseEntityRelationsGenerator multiEntityGenerator, DatabaseEntityGenerator singleEntityGenerator,Generator initGen) {
    super(multiEntityGenerator, singleEntityGenerator);
    this.inner=initGen;
  }
  private Generator inner;

  @Override
  public Generator withException() throws DataGenerationException {
    return inner.withException();
  }

  @Override
  public InnerCache cache() {
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
  public Generator generateByClass(Class<?> cl) {
    return super.generateByClass(cl);
  }

  @Override
  public Generator generateByTable(String schema, String table) {
    return super.generateByTable(schema, table);
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
    this.inner =  super.metronome(period, metric, predicate);
    return this;
  }

  @Override
  public Generator startId(long val) {
    return inner.startId(val);
  }


  @Override
  public Generator async() {
    throw new IllegalStateGeneratorException(" It can't possible invoking async for async generator.  ");
  }
}
