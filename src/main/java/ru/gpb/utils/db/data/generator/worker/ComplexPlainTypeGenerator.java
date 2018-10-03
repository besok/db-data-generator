package ru.gpb.utils.db.data.generator.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Complex generator for generating values with rules and default generator
 * @see DefaultPlainTypeGenerator
 * @see Generator#rule(ColumnPredicate, Action, Class)
 *
 * In that class we have delegate @see {@link DefaultPlainTypeGenerator} and rule dictionary for modifying values.
 * This generator is composed item where common job is maked by @see {@link DefaultPlainTypeGenerator}
 * and additional job by list of {@link Action}
 *
 *
 * Created by Boris Zhguchev on 01/10/2018
 */

// TODO: 10/1/2018 Тесты для всех классов + тесты с комплексными объектами.
public class ComplexPlainTypeGenerator extends AbstractPlainTypeGenerator {

  private PlainTypeGenerator delegate;

  protected Map<String, List<FilterAction<?>>> mapperMap;

  public ComplexPlainTypeGenerator() {
	delegate = new DefaultPlainTypeGenerator();
	mapperMap = new HashMap<>();
  }



  @Override
  @SuppressWarnings("unchecked")
  public Object generate(Class<?> clazz, MetaData.Column metaDataColumn) {
	Object val = delegate.generate(clazz, metaDataColumn);

	List<FilterAction<?>> actions = mapperMap.getOrDefault(clazz.getSimpleName(), new ArrayList<>());

	for (FilterAction action : actions) {
	  if(action.predicate.test(metaDataColumn))
	    val = action.action.process(val);
	}

	return val;
  }

  /**
   * set new rule @see {@link FilterAction}
   * @param <V> generated value type
   * @param predicate column predicate @see {@link ColumnPredicate}
   * @param action action @see {@link Action}
   * @param clzz return type for value . It needs for additional filtering by type.
   * */
  public <V> void setPair(ColumnPredicate predicate, Action<V> action, Class<V> clzz) {
	mapperMap.compute(
	  clzz.getSimpleName(),
	  (k, v) -> {
		if (v == null)
		  v = new ArrayList<>();
		v.add(new FilterAction<>(predicate, action));
		return v;
	  });
  }



}
