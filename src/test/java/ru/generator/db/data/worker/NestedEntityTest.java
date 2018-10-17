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
import ru.generator.db.data.worker.data.nested.NestedObject;
import ru.generator.db.data.worker.data.nested.NestedObjectRepository;
import ru.generator.db.data.worker.data.nested.SimpleObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by Boris Zhguchev on 16/10/2018
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class NestedEntityTest {
  @Autowired
  private NestedObjectRepository re;
  @Autowired
  private DatabaseDataGeneratorFactory factory;

  @Test
  public void nestedObjectTest() throws DataGenerationException {
	InnerLog log = factory
	  .generator()
	  .generateBy(NestedObject.class)
	  .withException()
	  .log();

	Assert.assertEquals(1,log.success());
	Assert.assertNotNull(re.findAll().get(0).getField());
  }
}
