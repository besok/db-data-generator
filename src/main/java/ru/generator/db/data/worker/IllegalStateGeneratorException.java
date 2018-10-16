package ru.generator.db.data.worker;
// 2018.07.24 

/**
 *
 * Declared exception
 * @author Boris Zhguchev
 */
public class IllegalStateGeneratorException extends RuntimeException {
  private String mes;

  public IllegalStateGeneratorException(String message) {
    super(message);
    this.mes=message;
  }

}
