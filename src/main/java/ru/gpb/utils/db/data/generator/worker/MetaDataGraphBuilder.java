package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * MetaData relations builder.
 * This builder builds neighbours relation(m2m) and before relations(m2o,o2o)
 * @see MetaData
 * @author Boris Zhguchev
 */
@Service
public class MetaDataGraphBuilder {
  private final MetaDataList metaDataList;


  @Autowired
  public MetaDataGraphBuilder(MetaDataList metaDataList) {
    this.metaDataList = metaDataList;
  }


  /**
   *
   * @throws DataGenerationException it is wrapper for {@link ClassNotFoundException}
   * */
  public void buildRelation() throws DataGenerationException {
    List<MetaData> mdList = this.metaDataList.getMetaDataList();

    for (MetaData md : mdList) {
      for (Field field : md.getDependencies().keySet()) {
        this.metaDataList.byClass(field.getType()).ifPresent(p -> md.getDependencies().putIfAbsent(field, p));
      }

      for (Field field : md.getNeighbours().keySet()) {
        try {
          this.metaDataList
              .byClass(fromColl(field))
              .ifPresent(p -> md.getNeighbours().put(field, p));
        } catch (ClassNotFoundException e) {
          throw new DataGenerationException(
              "Class not found = [" + md.getAClass().getSimpleName() + "] , field = [" + field.getName() + "]", e);
        }
      }
    }


  }

  private Class<?> fromColl(Field f) throws ClassNotFoundException {
    Type type = ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
    return Class.forName(type.getTypeName());
  }
}