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

  /**
   * Generate new instance for Class.
   * It tries to find this {@link MetaDataList#byClass(Class)}
   * Then it construct entity and relations.
   *
   * @param cl class for searching. Must have {@link javax.persistence.Entity} annotation.
   */
  @Override
  public Generator generateBy(Class<?> cl) {
    for (int i = 0; i < limit; i++) {
      super.generateBy(cl);
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
    for (int i = 0; i < limit; i++) {
      super.generateBy(schema, table);
    }
    return this;
  }

  /**
   * Generate new instance and relations for all founded Class
   **/
  @Override
  public Generator generateAll() {
    for (int i = 0; i < limit; i++) {
    super.generateAll();
    }
    return this;
  }

  /**
   * generate all instances except m2m relations.
   */
  @Override
  public Generator generateObjects() {
    for (int i = 0; i < limit; i++) {
      super.generateObjects();
    }
    return this;
  }

  /**
   * generate all relations except generating instances.
   * They can be found in the cache {@link InnerCache}
   */
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
