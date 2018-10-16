package ru.generator.db.data.worker;

/**
 * Data class for ColumnPredicate @see {@link ColumnPredicate} and Action {@link Action}
 * <p>
 * Created by Boris Zhguchev on 03/10/2018
 */
class FilterAction<V> {
  ColumnPredicate predicate;
  Action<V> action;

  public FilterAction(ColumnPredicate predicate, Action<V> action) {
	this.predicate = predicate;
	this.action = action;
  }
}
