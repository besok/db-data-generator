package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

/**
 *
 * Declared exception
 * @author Boris Zhguchev
 */
public class DataGenerationException extends Exception {
  private Exception exception;
  private String mes;
  public DataGenerationException(Exception exception) {
    this.exception = exception;
  }

  public DataGenerationException(String message, Exception exception) {
    super(message);
    this.mes=message;
    this.exception = exception;
  }

  @Override
  public String toString() {
    return "Ex["+exception+"] : "+mes;
  }
}
