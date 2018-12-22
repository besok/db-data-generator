package ru.generator.db.data.worker.data.withoutrepo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.generator.db.data.worker.DataGenerationException;
import ru.generator.db.data.worker.DatabaseDataGeneratorFactory;
import ru.generator.db.data.worker.NoRepositoryException;

import static org.junit.Assert.*;

/**
 * Created by Boris Zhguchev on 22/12/2018
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class WithoutRepoTest {

  @Autowired
  private DatabaseDataGeneratorFactory factory;
  @Autowired
  private WithRepoRepository repository;

  @Test(expected = NoRepositoryException.class)
  public void withoutRepoRelatedExceptionTest() throws DataGenerationException {

	factory.generator()
	  .generateBy(WithRepo.class)
	  .withException()
	;
  }

  @Test(expected = DataGenerationException.class)
  public void withoutRepoExceptionTest() throws DataGenerationException {
	factory.generator()
	  .generateBy(WithoutRepo.class)
	  .withException();
  }
}