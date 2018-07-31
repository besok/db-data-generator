package ru.gpb.utils.db.data.generator.worker;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * BeanPostProcessor
 * for processing JpaRepositoryBeans @see {@link org.springframework.data.jpa.repository.JpaRepository}
 * This processor tries to find all JpaRepo then get entity class @see {@link Entity}
 * then get information about database properties(table,schema name, columns and etc)
 *
 * <div>
 * The field optional exists is very important for {@link OneToOne} relation.
 * One of that tables must be optional.
 * </div>
 *
 * @author Boris Zhguchev
 */
@Component
public class DatabaseMetadataScanBeanPostProcessor implements BeanPostProcessor {
  private final MetaDataList metaDataList;

  @Autowired
  public DatabaseMetadataScanBeanPostProcessor(MetaDataList metaDataList) {
    this.metaDataList = metaDataList;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof JpaRepositoryFactoryBean) {


      EntityInformation ei = ((JpaRepositoryFactoryBean) bean).getEntityInformation();
      Class<?> jt = ei.getJavaType();

      Table tbl = jt.getDeclaredAnnotation(Table.class);
      MetaData metaData = new MetaData();
      metaData.setAClass(jt);
      metaData.setHeader(jt.getName(), tbl.name(), tbl.schema());
      metaData.setDependencies(new HashMap<>());
      metaData.setNeighbours(new HashMap<>());
      metaData.setPlainColumns(new HashSet<>());
      metaData.setPlain(false);

      Field[] fields = jt.getDeclaredFields();
      for (Field f : fields) {
        f.setAccessible(true);
        boolean plain = true;


        if (f.isAnnotationPresent(Id.class)) metaData.setIdField(f);

        if (f.isAnnotationPresent(ManyToOne.class)) {
          metaData.getDependencies().put(f, null);
          plain = false;
        }
        if (f.isAnnotationPresent(OneToOne.class)) {
          plain = false;
          OneToOne an = f.getDeclaredAnnotation(OneToOne.class);
          if (!an.optional()) metaData.getDependencies().put(f, null);
        }

        if (isNeighbour(f)) {
          plain = false;
          metaData.getNeighbours().put(f, null);
        }

        if (plain) {
          if (f.isAnnotationPresent(Column.class)) {
            Column col = f.getAnnotation(Column.class);
            metaData.addPlainColumn(f.getName(), col.name(), col.length(), f.getType(), col.nullable(), isCollection(f));
          } else {
            metaData.addPlainColumn(f.getName(), f.getName(), 0, f.getType(), true, isCollection(f));
          }
        }
      }

      if (metaData.getNeighbours().size() == 0 && metaData.getDependencies().size() == 0)
        metaData.setPlain(true);
      metaDataList.add(metaData);
    }

    return bean;
  }


  private boolean isNeighbour(Field f) {
    return f.isAnnotationPresent(ManyToMany.class);
  }

  private boolean isCollection(Field field) {
    return Collection.class.isAssignableFrom(field.getType());
  }
}
