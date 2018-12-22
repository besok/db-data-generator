package ru.generator.db.data.worker;
// 2018.07.24 

/**
 *
 * Declared exception
 * @author Boris Zhguchev
 */
public class DataGenerationException extends Exception {
  private Throwable exception;
  private String mes;
  public DataGenerationException(Throwable exception) {
    this.exception = exception;
  }

  public DataGenerationException(String message, Exception exception) {
    super(message,exception);
    this.mes=message;
    this.exception = exception;
  }

  @Override
  public String toString() {
    return "Ex["+exception+"] : "+mes;
  }
}
