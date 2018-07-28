package ru.gpb.utils.db.data.generator.worker;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObjectRepository;

import java.util.List;
import java.util.logging.Logger;

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
        .repeate(10)
        .generateByClass(SimplePlainObject.class)
        .cache();
  }

}

