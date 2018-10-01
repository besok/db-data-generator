package ru.gpb.utils.db.data.generator.worker;

/**
 * Created by Boris Zhguchev on 01/10/2018
 */
/**
 * It is a predicate for stopping work. @See {@link MetronomePredicate}
 * */
public interface MetronomePredicate {
  /**
   * @param ctx context from around class
   * */
  boolean test(MetronomeGenerator ctx);
  static MetronomePredicate COUNT(int count){
	return ctx -> ctx.log().marker() < count;
  }
  static MetronomePredicate COUNT_SUCCESS(int count){
	return ctx -> ctx.log().success() < count;
  }
  static MetronomePredicate COUNT_FAILURE(int count){
	return ctx -> ctx.log().failure() < count;
  }
}