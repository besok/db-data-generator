package ru.generator.db.data.worker;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.generator.db.data.worker.data.constraints.EntityLeft;
import ru.generator.db.data.worker.data.constraints.EntityRight;
import ru.generator.db.data.worker.data.constraints.LeftRightRel;
import ru.generator.db.data.worker.data.constraints.LeftRightRelRepository;

import java.util.Comparator;
import java.util.List;

import static ru.generator.db.data.worker.Action.*;

/**
 * Created by Boris Zhguchev on 22/12/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"generator.always-new:true"})
public class ConstraintTest {

  @Autowired
  private DatabaseDataGeneratorFactory factory;

  @Autowired
  private LeftRightRelRepository repository;

  @Test
  public void uniqueConstraintTest() throws DataGenerationException {
	factory.generator()
	  .repeate(5)
	  .generateBy(EntityLeft.class)
	  .generateBy(EntityRight.class)
	  .repeate(30)
	  .generateBy(LeftRightRel.class)
	  .withException();


  }
}
