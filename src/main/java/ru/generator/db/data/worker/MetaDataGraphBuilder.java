package ru.generator.db.data.worker;
// 2018.07.24 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiFunction;

/**
 * MetaData relations builder.
 * This builder builds neighbours relation(m2m) and before relations(m2o,o2o)
 *
 * @author Boris Zhguchev
 * @see MetaData
 */
@Service
public class MetaDataGraphBuilder {
  private final MetaDataList mdList;


  @Autowired
  public MetaDataGraphBuilder(MetaDataList mdList) {
	this.mdList = mdList;
  }


  /**
   * @throws DataGenerationException it depends on class neighbours.
   */
  public void buildRelation() throws DataGenerationException {
	List<MetaData> mdList = this.mdList.getMetaDataList();

	for (MetaData md : mdList) {
	  for (Field field : md.getDependencies().keySet()) {
		this.mdList
		  .byClass(field.getType())
		  .ifPresent(p -> md.getDependencies().compute(field, compute(p)));
	  }

	  for (Field field : md.getNeighbours().keySet()) {
		try {
		  this.mdList.byClass(fromColl(field)).ifPresent(p -> md.getNeighbours().put(field, p));
		} catch (ClassNotFoundException e) {
		  throw new DataGenerationException(
			"Class not found = [" + md.getAClass().getSimpleName() + "] , field = [" + field.getName() + "]", e);
		}
	  }
	}


  }

  // we have to reverse JoinPrimaryKey flag because it shows parent class but we have to get child class.
  private BiFunction<Field, MetaData.Dependency, MetaData.Dependency> compute(MetaData p) {
	return (k, v) -> {
	  boolean forJoinPrimaryKey = v.isForJoinPrimaryKey();
	  v.setForJoinPrimaryKey(!forJoinPrimaryKey);
	  return MetaData.Dependency.of(p, v.isOptional(), v.isAlwaysNew(), !forJoinPrimaryKey);
	};
  }

  private Class<?> fromColl(Field f) throws ClassNotFoundException {
	Type type = ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
	return Class.forName(type.getTypeName());
  }
}