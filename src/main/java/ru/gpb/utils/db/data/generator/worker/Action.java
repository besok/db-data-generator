package ru.gpb.utils.db.data.generator.worker;

/**
 *
 * Action class for changing output generated value.
 * For more detailed info, please, @see {@link ComplexPlainTypeGenerator}
 *
 * This interface is SAM interface.
 *
 * @param <V> generated value type.
 *
 * Created by Boris Zhguchev on 01/10/2018
 */
public interface Action<V> {

  /**
   * method for changing or modifying generated value.
   * @param oldValue generated value from default generator @see {@link DefaultPlainTypeGenerator#generate(Class, MetaData.Column)}
   * @return new value or modified oldValue
   *
   * */
  V process(V oldValue);

  /**
   * Simplify template ops.
   *
   * */

  static <V> Action<V> CONST(V v){return oldValue -> v ;}
  static <V> Action<V> OLD(){ return oldValue -> oldValue;}
}
