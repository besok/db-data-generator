package ru.generator.db.data.worker;
// 2018.07.24 

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *
 * Metadatas with relations @see {@link MetaData}
 * @author Boris Zhguchev
 */
@Data
@Component
public class MetaDataList {

  private List<MetaData> metaDataList;

  public MetaDataList() {
    this.metaDataList = new ArrayList<>();
  }

  public void add(MetaData metaData){
    metaDataList.add(metaData);
  }
  public Optional<MetaData> byClass(Class<?> clazz){
    for (MetaData p : metaDataList) {
      if(Objects.equals(p.getAClass(),clazz))
        return Optional.of(p);
    }
    return Optional.empty();
  }
  public Optional<MetaData> bySchemaTable(String schema, String table){
    for (MetaData metaData : metaDataList) {
      String s = metaData.getHeader().getSchema();
      String t = metaData.getHeader().getTable();
      if(Objects.equals(s,schema) && Objects.equals(t,table))
        return Optional.of(metaData);
    }
    return Optional.empty();
  }
}
