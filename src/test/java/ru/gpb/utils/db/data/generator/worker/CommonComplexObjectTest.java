package ru.gpb.utils.db.data.generator.worker;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.*;

import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.*;
import static ru.gpb.utils.db.data.generator.worker.MetronomeGenerator.MetronomePredicate.COUNT;

// 2018.07.24 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CommonComplexObjectTest {


  @Autowired
  private DatabaseDataGeneratorFactory factory;
  @Autowired
  private MetaDataList mdl;

  @Test
  public void generateEntWithManyToManyReleationTest() throws DataGenerationException {
    Map<MetaData, Integer> snapshot =
        factory
            .generator().async()
            .generateBy(ManyToManyObjectLeft.class)
            .generateBy(ManyToManyObjectRight.class)
            .withException()
            .cache().snapshot();

    Integer lCount = snapshot.get(mdl.byClass(ManyToManyObjectLeft.class).get());
    Integer rCount = snapshot.get(mdl.byClass(ManyToManyObjectRight.class).get());

    assertEquals(1, lCount.intValue());
    assertEquals(1, rCount.intValue());
  }

}

