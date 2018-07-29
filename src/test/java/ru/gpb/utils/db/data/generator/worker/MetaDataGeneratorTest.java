package ru.gpb.utils.db.data.generator.worker;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.MetronomeGenerator.MetronomePredicate;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObjectRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static ru.gpb.utils.db.data.generator.worker.MetronomeGenerator.MetronomePredicate.*;

// 2018.07.24 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MetaDataGeneratorTest {

  private Logger LOGGER = Logger.getLogger(MetaDataGeneratorTest.class.getName());

  @Autowired
  private DatabaseDataGeneratorFactory factory;
  @Autowired
  private SimplePlainObjectRepository repository;

  @Test
  public void simpleObjectTest() {
    long marker =
        factory
            .generator()
            .repeate(10)
            .generateByClass(SimplePlainObject.class)
            .log().markerValue();

    List<SimplePlainObject> objs = repository.findAll();

    Assert.assertEquals(marker / 2, objs.size());
  }

  @Test
  public void multiGenerate() {
    InnerCache cache = factory
        .generator()
        .metronome(10,TimeUnit.MILLISECONDS)
        .predicate(countPredicate(20))
        .generateByClass(SimplePlainObject.class)
        .cache();

    List<Object> valueList = cache.getValueList(SimplePlainObject.class);
    valueList.forEach(System.out::println);
  }

}

