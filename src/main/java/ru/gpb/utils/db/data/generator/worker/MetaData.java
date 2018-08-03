package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24

import lombok.*;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Default data object.
 *
 * @author Boris Zhguchev
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class MetaData {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MetaData metaData = (MetaData) o;
    return Objects.equals(aClass, metaData.aClass) &&
        Objects.equals(header, metaData.header);
  }

  @Override
  public int hashCode() {
    return Objects.hash(aClass, header);
  }

  private Class<?> aClass;
  private Header header;
  private boolean plain;
  private Id id;
  private Map<Field, MetaData> dependencies;
  private Map<Field, MetaData> neighbours;
  private Set<Column> plainColumns;

  public MetaData dependency(Field f) {
    return dependencies.get(f);
  }

  public MetaData neighbour(Field f) {
    return neighbours.get(f);
  }

  @AllArgsConstructor
  @Getter
  public class Header {
    private String name;
    private String table;
    private String schema;

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
  }

  @AllArgsConstructor
  @Getter
  public class Id{
    private Field idField;
    private boolean generated;
  }

  public Optional<Column> findByField(Field field) {
    for (Column c : plainColumns) {
      if (Objects.equals(field.getName(), c.getField()))
        return Optional.of(c);
    }
    return Optional.empty();
  }
  public void addId(Field id,boolean generated){
    this.id = new Id(id,generated);
  }
  public void addPlainColumn(
      String field,
      String column,
      int length,
      Class<?> aClass,
      boolean nullable,
      boolean collection,
      Field f
  ) {
    plainColumns.add(new Column(field,column,length,aClass,f,nullable,collection));
  }

  public void setHeader(String className, String tableName, String schemaName) {
    header = new Header(className, tableName, schemaName);
  }


  public boolean isId(Field field){
    return id.idField.equals(field);
  }


}
