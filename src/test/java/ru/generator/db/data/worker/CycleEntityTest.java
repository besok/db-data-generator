package ru.generator.db.data.worker;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.generator.db.data.worker.data.cycle.CycleObject3;
import ru.generator.db.data.worker.data.cycle.CycleObjectOneToOne1;
import ru.generator.db.data.worker.data.cycle.CycleObjectOneToOne2;
import ru.generator.db.data.worker.data.cycle.CycleObjectRepository;

import java.util.List;
import java.util.Objects;

/**
 * Created by Boris Zhguchev on 16/10/2018
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CycleEntityTest {
  @Autowired
  private CycleObjectRepository re;
  @Autowired
  private DatabaseDataGeneratorFactory factory;


  @Test
  public void manyToOneCycleTest() throws DataGenerationException {
	List<CycleObject3> chObjs = factory
	  .generator()
	  .repeate(3)
	  .generateBy(CycleObject3.class)
	  .withException()
	  .cache()
	  .getValueList(CycleObject3.class);

	List<CycleObject3> dbObjs = re.findAll();

	Assert.assertEquals(chObjs.size(), dbObjs.size());
	Assert.assertTrue(notNullSelf(dbObjs));
	Assert.assertTrue(notNullCyclic(dbObjs));

  }


  @Test
  public void oneToOneCycleTest() throws DataGenerationException {
	String report = factory
	  .generator()
	  .repeate(3)
	  .generateBy(CycleObjectOneToOne1.class)
	  .generateBy(CycleObjectOneToOne2.class)
	  .withException()
	  .report();

	System.out.println(report);
  }

  private boolean notNullSelf(List<CycleObject3> dbObjs) {
	return dbObjs.stream().anyMatch(el -> Objects.nonNull(el.getSelfCycleField()));
  }

  private boolean notNullCyclic(List<CycleObject3> dbObjs) {
	return dbObjs.stream().anyMatch(el -> Objects.nonNull(el.getCycleField()));
  }
}
