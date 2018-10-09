package ru.gpb.utils.db.data.generator.worker;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Action class for changing output generated value.
 * For more detailed info, please, @see {@link ComplexPlainTypeGenerator}
 * <p>
 * This interface is SAM interface.
 *
 * @param <V> generated value type.
 *            <p>
 *            Created by Boris Zhguchev on 01/10/2018
 */
public interface Action<V> {

  /**
   * Return constant value
   */
  static <V> Action<V> CONST(V v) {
	return oldValue -> v;
  }

  /**
   * Without change.Return old value.
   */
  static <V> Action<V> OLD() {
	return oldValue -> oldValue;
  }



  static <V> Action<V> PEEK(Consumer<V> peekFn) {
	return oldValue -> {
	  peekFn.accept(oldValue);
	  return oldValue;
	};
  }

  static Action<Integer> INCREMENT_I(int startVal) {
	return new Action<Integer>() {
	  private AtomicInteger delegate = new AtomicInteger(startVal);

	  @Override
	  public Integer process(Integer oldValue) {
		return delegate.incrementAndGet();
	  }
	};
  }

  static Action<Long> INCREMENT_L(int startVal) {
	return new Action<Long>() {
	  private AtomicLong delegate = new AtomicLong(startVal);

	  @Override
	  public Long process(Long oldValue) {
		return delegate.incrementAndGet();
	  }
	};
  }

  @SuppressWarnings("unchecked")
  static <V> Action<V> RANDOM(int bound) {
	return (Action<V>) new Action<Object>() {
	  private Random random = new Random();

	  @Override
	  public Object process(Object oldValue) {
		switch (oldValue.getClass().getSimpleName()) {
		  case "long":
		  case "Long":
		  case "int":
		  case "Integer":
			return random.nextInt(bound);
		  case "Double":
		  case "double":
			return (double) random.nextInt(bound);
		  case "Float":
		  case "float":
			return (float) random.nextInt(bound);
		  case "boolean":
		  case "Boolean":
			return random.nextBoolean();
		  case "String":
			return String.valueOf(random.nextInt(bound));
		}
		return oldValue;
	  }
	};
  }

  /**
   * method for changing or modifying generated value.
   *
   * @param oldValue generated value from default generator @see {@link DefaultPlainTypeGenerator#generate(Class, MetaData.Column)}
   * @return new value or modified oldValue
   */
  V process(V oldValue);

}
