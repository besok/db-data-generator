package ru.gpb.utils.db.data.generator.worker;

import java.util.stream.Stream;

/**
 * Predicate for changing output generated value if this predicate is true.
 * For more detailed info, please, @see {@link ComplexPlainTypeGenerator}
 * <p>
 * This interface is SAM interface.
 * <p>
 * Created by Boris Zhguchev on 01/10/2018
 */
public interface ColumnPredicate {

  /**
   * utility methods for doing some ops easy.
   */

  static ColumnPredicate CLASS(Class<?> clzz) {
	return column -> column.getParent().getAClass().equals(clzz);
  }


  static ColumnPredicate FIELD(String name) {
	return column -> column.getField().equals(name);
  }

  static ColumnPredicate FIELD(String... names) {
	return column -> Stream.of(names).anyMatch(n -> n.equals(column.getField()));
  }

  static ColumnPredicate COLUMN(String column) {
	return col -> col.getColumn().equals(column);
  }

  static ColumnPredicate COMPOSE(ColumnPredicate... predicate) {
	return column -> {
	  for (ColumnPredicate p : predicate) {
		if (!p.test(column))
		  return false;
	  }
	  return true;
	};
  }

  /**
   * method for checking condition for changing generated value
   *
   * @param column column corresponding generated value.
   * @return boolean : if true the generated value will be proccesed by @see {@link Action#process(Object)}
   */
  boolean test(MetaData.Column column);



}
