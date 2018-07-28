package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24

import lombok.*;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
 * Default data object.
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
  private Map<Field, MetaData> dependencies;
  private Map<Field, MetaData> neighbours;
  private Field idField;

  public MetaData dependency(Field f) {
    return dependencies.get(f);
  }
  public MetaData neighbour(Field f) {
    return neighbours.get(f);
  }

  @AllArgsConstructor
  @Getter
  @Setter
  public class Header {
    private String name;
    private String table;
    private String schema;

    public String toString() {
      return "[" + name + "][" + schema + "." + table + "]";
    }
  }

  public void setHeader(String className, String tableName, String schemaName) {
    header = new Header(className, tableName, schemaName);
  }
}
