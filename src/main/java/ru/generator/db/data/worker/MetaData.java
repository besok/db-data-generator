package ru.generator.db.data.worker;
// 2018.07.24

import lombok.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * meta data object.
 *
 * @author Boris Zhguchev
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class MetaData {
  private Class<?> aClass;
  private Header header;
  private boolean plain;
  private Id id;
  private Map<Field, Dependency> dependencies;
  private Map<Field, MetaData> neighbours;
  private List<Column> plainColumns;

  @Override
  public boolean equals(Object o) {
	if (this == o) return true;
	if (o == null || getClass() != o.getClass()) return false;
	MetaData metaData = (MetaData) o;
	return Objects.equals(aClass, metaData.aClass) &&
	  Objects.equals(header, metaData.header);
  }

  public Optional<MetaData> findForId() {
	return this.dependencies.values().stream()
	  .filter(Dependency::isForJoinPrimaryKey)
	  .map(Dependency::getMd)
	  .findAny();
  }

  public Object getIdValue(Object entity) {
	Field idField = this.getId().idField;
	try {
	  return idField.get(entity);
	} catch (IllegalAccessException e) {
	  throw new IllegalStateGeneratorException(e, "get error from id field");
	}
  }

  public Object getValue(Object entity, Field field) {
	try {
	  return field.get(entity);
	} catch (IllegalAccessException e) {
	  throw new IllegalStateGeneratorException(e, "get error from field " + field.getName());
	}
  }

  public Object setIdValue(Object entity, Object idValue) {
	try {
	  id.getIdField().set(entity, idValue);
	  return entity;
	} catch (IllegalAccessException e) {
	  throw new IllegalStateGeneratorException(e, "get error from id field");
	}
  }

  public Object setDependencyValue(Object entity, MetaData fieldMetaData, Object value) {
	for (Map.Entry<Field, Dependency> e : dependencies.entrySet()) {
	  if (e.getValue().getMd().equals(fieldMetaData)) {
		try {
		  e.getKey().set(entity, value);
		} catch (IllegalAccessException e1) {
		  throw new IllegalStateGeneratorException(e1, "get error from field" );
		}
	  }
	}
	return entity;
  }

  @Override
  public int hashCode() {
	return Objects.hash(aClass, header);
  }

  public Dependency dependency(Field f) {
	return dependencies.get(f);
  }

  public MetaData neighbour(Field f) {
	return neighbours.get(f);
  }

  Optional<Column> findByField(Field field) {
	for (Column c : plainColumns) {
	  if (Objects.equals(field.getName(), c.field))
		return Optional.of(c);
	}
	return Optional.empty();
  }

  void addId(Field id, boolean generated,String column) {
	this.id = new Id(id, generated,column);
  }

  void addPlainColumn(
	String field,
	String column,
	int length,
	Class<?> aClass,
	boolean nullable,
	boolean collection,
	Field f,
	int precision, int scale
  ) {
	plainColumns.add(new Column(field, column, length, aClass, f, nullable, collection, this, precision, scale));
  }

  void setHeader(String className, String tableName, String schemaName) {
	header = new Header(className, tableName, schemaName);
  }

  boolean isId(Field field) {
	return id.idField.equals(field);
  }

  @AllArgsConstructor(staticName = "of")
  @Getter
  @Setter
  public static class Dependency {
	private MetaData md;
	private boolean optional;
	private boolean alwaysNew;
	private boolean forJoinPrimaryKey;
	private String column;
  }

  @Getter
  public class Header {
	private String name;
	private String table;
	private String schema;

	public Header(String name, String table, String schema) {
	  this.name = name;
	  this.table = table;
	  this.schema = schema;
	}

	public String tbl() {
	  return schema + "." + table;
	}

	public String toString() {
	  return "[" + name + "][" + schema + "." + table + "]";
	}
  }

  @Getter
  @Setter
  @AllArgsConstructor
  public class Column {
	private String field;
	private String column;
	private int length;
	private Class<?> aClass;
	private Field val;
	private boolean nullable;
	private boolean collection;
	private MetaData parent;
	private int precision;
	private int scale;

  }

  @AllArgsConstructor
  @Getter
  public class Id {
	private Field idField;
	private boolean generated;
	private String column;

  }
}
