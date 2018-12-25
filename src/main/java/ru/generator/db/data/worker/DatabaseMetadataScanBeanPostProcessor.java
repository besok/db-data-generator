package ru.generator.db.data.worker;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.*;

import static ru.generator.db.data.worker.MetaData.Dependency.*;

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
	  MetaData metaData = new MetaData();
	  metaData.setAClass(jt);

	  if (jt.isAnnotationPresent(Table.class)) {
		Table tbl = jt.getDeclaredAnnotation(Table.class);
		metaData.setHeader(jt.getName(), tbl.name(), tbl.schema());
	  } else {
		metaData.setHeader(jt.getName(), fromClass(jt), "");
	  }
	  initMd(metaData);

	  Field[] fields = jt.getDeclaredFields();
	  for (Field f : fields) {
		f.setAccessible(true);
		boolean plain = true;

		if (!checkIdField(metaData, f)) {
		  if (f.isAnnotationPresent(ManyToOne.class)) {
			plain = processManyToOne(metaData, f);
		  }
		  if (f.isAnnotationPresent(OneToOne.class)) {
			plain = processOneToOne(metaData, f);
		  }
		  if (f.isAnnotationPresent(ManyToMany.class)) {
			plain = false;
			metaData.getNeighbours().put(f, null);
		  }

		  if (plain) {
			processPlainObj(metaData, f);
		  }
		}
	  }
	  if (metaData.getNeighbours().size() == 0 && metaData.getDependencies().size() == 0)
		metaData.setPlain(true);
	  metaDataList.add(metaData);

	}
	return bean;
  }

  private String fromClass(Class<?> jt) {
	String name = jt.getSimpleName();
	return name.substring(0, 1).toLowerCase() + name.substring(1);
  }

  private boolean processOneToOne(MetaData metaData, Field f) {
	OneToOne an = f.getDeclaredAnnotation(OneToOne.class);
	boolean joinPrimaryKey = false;
	if (f.isAnnotationPresent(PrimaryKeyJoinColumn.class)) {
	  joinPrimaryKey = true;
	}
	metaData
	  .getDependencies()
	  .put(f, of(null, an.optional(), true, joinPrimaryKey));
	return false;
  }

  private boolean processManyToOne(MetaData metaData, Field f) {
	ManyToOne an = f.getDeclaredAnnotation(ManyToOne.class);
	metaData.getDependencies().put(f, of(null, an.optional(), false,false));
	return false;
  }

  private void initMd(MetaData metaData) {
	metaData.setDependencies(new HashMap<>());
	metaData.setNeighbours(new HashMap<>());
	metaData.setPlainColumns(new HashSet<>());
	metaData.setPlain(false);
  }

  private void processPlainObj(MetaData metaData, Field f) {
	if (f.isAnnotationPresent(Column.class)) {
	  Column col = f.getAnnotation(Column.class);
	  metaData.addPlainColumn(
		f.getName(),
		col.name().equals("") ? camelToSnake(f.getName()) : col.name(),
		col.length(), f.getType(), col.nullable(), isCollection(f),
		f,col.precision(),col.scale());
	} else {
	  metaData.addPlainColumn(f.getName(), camelToSnake(f.getName()), 0, f.getType(), true,
		isCollection(f), f,0,0);
	}
  }

  private boolean checkIdField(MetaData metaData, Field f) {
	if (f.isAnnotationPresent(Id.class)) {
	  if (f.isAnnotationPresent(GeneratedValue.class)) {
		metaData.addId(f, true);
	  } else {
		metaData.addId(f, false);
	  }
	  return true;
	}
	return false;
  }

  private boolean isCollection(Field field) {
	return Collection.class.isAssignableFrom(field.getType());
  }

  private String camelToSnake(String field) {
	Queue<Character> q = new ArrayDeque<>();
	int i = 0;
	for (char c : field.toCharArray()) {
	  if (Character.isUpperCase(c)) {
		if (i != 0) {
		  q.add('_');
		}
		q.add(Character.toLowerCase(c));
	  } else {
		q.add(c);
	  }
	  i++;
	}
	StringBuilder sb = new StringBuilder();
	for (Character ch : q) {
	  sb.append(ch);
	}
	return sb.toString();
  }

  // TODO: 20.12.2018 process cascades
}
