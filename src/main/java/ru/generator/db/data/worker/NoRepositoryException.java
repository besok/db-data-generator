package ru.generator.db.data.worker;

/**
 * Created by Boris Zhguchev on 22/12/2018
 */
public class NoRepositoryException extends DataGenerationException {
  public NoRepositoryException(String entity) {
	super("No repository for entity "+entity, new NullPointerException());
  }
}
