package ru.gpb.utils.db.data.generator.worker;

/**
 *
 * Predicate for changing output generated value if this predicate is true.
 * For more detailed info, please, @see {@link ComplexPlainTypeGenerator}
 *
 * This interface is SAM interface.
 *
 * Created by Boris Zhguchev on 01/10/2018
 */
public interface ColumnPredicate {

  /**
   * method for checking condition for changing generated value
   * @param column column corresponding generated value.
   * @return boolean : if true the generated value will be proccesed by @see {@link Action#process(Object)}
   *
   * */
  boolean test(MetaData.Column column);


  /**
   * utility methods for doing some ops easy.
   * */

  static ColumnPredicate CLASS(Class<?> clzz) {
	return column -> column.getParent().getAClass().equals(clzz);
  }

  static ColumnPredicate FIELD(String name) {
	return column -> column.getField().equals(name);
  }

  static ColumnPredicate COLUMN(String column){return col -> col.getColumn().equals(column);}

  static ColumnPredicate COMPOSE(ColumnPredicate... predicate) {
	return column -> {
	  for (ColumnPredicate p : predicate) {
		if (!p.test(column))
		  return false;
	  }
	  return true;
	};
  }


}
