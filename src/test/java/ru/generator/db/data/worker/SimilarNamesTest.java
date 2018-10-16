package ru.generator.db.data.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.similar.ForSimilarObject;

/**
 * Created by Boris Zhguchev on 16/10/2018
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SimilarNamesTest {

  @Autowired
  private DatabaseDataGeneratorFactory factory;


  @Test
  @Repeat(50)
  public void similarNameTest() throws DataGenerationException {
	String report = factory
	  .generator()
	  .repeate(50)
	  .generateBy(ForSimilarObject.class)
	  .withException()
	  .report()
	  ;

	System.out.println(report);
  }
}
