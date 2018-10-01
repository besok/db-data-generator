package ru.gpb.utils.db.data.generator.worker;
// 2018.07.25 


/**
 * Generator for repeating events.
 *
 * @author Boris Zhguchev
 */
public class RepeatableGenerator extends Generator {
  private int limit;

  RepeatableGenerator(DatabaseEntityRelationsGenerator multiEntityGenerator, DatabaseEntityGenerator singleEntityGenerator, int cycles) {
	super(multiEntityGenerator, singleEntityGenerator);
	this.limit = cycles;
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
	super.rule(predicate, action, vClass);
	return this;
  }

  @Override
  Generator split() {
	return super.repeate(this.limit);
  }
}
