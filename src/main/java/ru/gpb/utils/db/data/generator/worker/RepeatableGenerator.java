package ru.gpb.utils.db.data.generator.worker;
// 2018.07.25 

/**
 *
 * Generator for repeating events.
 *
 * @author Boris Zhguchev
 */
public class RepeatableGenerator extends Generator {
  RepeatableGenerator(DatabaseEntityRelationsGenerator multiEntityGenerator, DatabaseEntityGenerator singleEntityGenerator, int cycles) {
    super(multiEntityGenerator, singleEntityGenerator);
    this.limit = cycles;
  }

  private int limit;


  @Override
  public Generator generateBy(Class<?> cl) {
    for (int i = 0; i < limit; i++) {
      super.generateBy(cl);
    }
    return this;
  }

  @Override
  public Generator generateBy(String schema, String table) {
    for (int i = 0; i < limit; i++) {
      super.generateBy(schema, table);
    }
    return this;
  }

  @Override
  public Generator generateAll() {
    for (int i = 0; i < limit; i++) {
    super.generateAll();
    }
    return this;
  }

  @Override
  public Generator generateObjects() {
    for (int i = 0; i < limit; i++) {
      super.generateObjects();
    }
    return this;
  }

  @Override
  public Generator generateRelations() {
    for (int i = 0; i < limit; i++) {
    super.generateRelations();
    }
    return this;
  }

  @Override
  Generator split() {
    return super.repeate(this.limit);
  }
}
