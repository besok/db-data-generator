package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Boris Zhguchev
 */
@Service
@SuppressWarnings("unchecked")
public class DatabaseEntityRelationsGenerator {

  private Logger LOGGER = Logger.getLogger(DatabaseDataGeneratorFactory.class.getName());

  @Getter
  protected final InnerCache cache;


  @Autowired
  public DatabaseEntityRelationsGenerator(InnerCache cache) {
    this.cache = cache;
  }

  protected void generateMultiObjects(MetaData metaData) throws DataGenerationException {
    for (Object valueList : cache.getValueList(metaData)) {
      for (Map.Entry<Field, MetaData> neigbourEntry : metaData.getNeighbours().entrySet()) {
        processCollection(metaData, valueList, neigbourEntry.getKey(), neigbourEntry.getValue());
      }
    }
  }

  private void processCollection(MetaData metaData, Object e, Field f, MetaData rightMetaData) throws DataGenerationException {
    if (Collection.class.isAssignableFrom(f.getType())) {
      f.setAccessible(true);
      try {
        Object collection = f.get(e);
        if (Objects.isNull(collection)) {
          collection = createColFromInterface(f.getType()).newInstance();
        }
        ((Collection) collection).addAll(cache.getValueList(rightMetaData));
        save(metaData, e);
      } catch (InstantiationException | IllegalAccessException ex) {
        LOGGER.info("exception's been caught: "+e.getClass().getSimpleName() + " for "+metaData.getAClass().getSimpleName() );
        throw new DataGenerationException("Reflection exception ", ex);
      }
    }
  }

  private void save(MetaData metaData, Object o) throws DataGenerationException {
    Optional<Object> rep = cache.repositories.getRepositoryFor(metaData.getAClass());
    if (rep.isPresent()) {
      try {
        ((JpaRepository) rep.get()).save(o);
      } catch (DataIntegrityViolationException e) {
        LOGGER.info("exception's been caught: "+e.getClass().getSimpleName() + " for "+metaData.getAClass().getSimpleName() );
        throw new DataGenerationException("in most cases relation has already been", e);
      }
    } else
      throw new DataGenerationException("repository not found", new IllegalArgumentException());

  }

  private Class<?> createColFromInterface(Class<?> cl) {
    switch (cl.getSimpleName()) {
      case "Set":
        return HashSet.class;
      case "List":
        return ArrayList.class;
    }
    return cl;
  }


}
