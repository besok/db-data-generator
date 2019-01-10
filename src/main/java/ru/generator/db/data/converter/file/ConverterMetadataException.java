package ru.generator.db.data.converter.file;

/**
 * Created by Boris Zhguchev on 10/01/2019
 */
public class ConverterMetadataException extends RuntimeException {
  public ConverterMetadataException(String message) {
	super(message);
  }

  public ConverterMetadataException() {
    super("metadata not found");
  }
}
