package ru.gpb.utils.db.data.generator.worker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.cycle.CycleObject3;
import ru.gpb.utils.db.data.generator.worker.data.cycle.CycleObjectRepository;

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
  public void cycleTest() throws DataGenerationException {
	List<CycleObject3> chObjs = factory
	  .generator()
	  .repeate(10)
	  .generateBy(CycleObject3.class)
	  .withException()
	  .cache()
	  .getValueList(CycleObject3.class);

	List<CycleObject3> dbObjs = re.findAll();

	Assert.assertEquals(chObjs.size(), dbObjs.size());
	Assert.assertTrue(notNullSelf(dbObjs));
	Assert.assertTrue(notNullCyclic(dbObjs));

  }

  private boolean notNullSelf(List<CycleObject3> dbObjs) {
	return dbObjs.stream().anyMatch(el -> Objects.nonNull(el.getSelfCycleField()));
  }

  private boolean notNullCyclic(List<CycleObject3> dbObjs) {
	return dbObjs.stream().anyMatch(el -> Objects.nonNull(el.getCycleField()));
  }
}
