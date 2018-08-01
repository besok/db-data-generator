package ru.gpb.utils.db.data.generator.worker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.MetronomeGenerator.MetronomePredicate;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObjectRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.*;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;
import static ru.gpb.utils.db.data.generator.worker.MetronomeGenerator.MetronomePredicate.*;

// 2018.07.24 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MetaDataGeneratorTest {


  @Autowired
  private DatabaseDataGeneratorFactory factory;
  @Autowired
  private SimplePlainObjectRepository repository;

  @Before
  public void clean() {
    repository.deleteAll();
  }

  @Test
  @Repeat(5)
  public void generatePlainObjectsCountTest() {
    long marker = factory
        .generator()
        .repeate(5)
        .generateByClass(SimplePlainObject.class)
        .log()
        .markerValue();

    assertEquals(marker / 2, repository.findAll().size());
  }

  @Test
  @Repeat(5)
  public void generatePlainObjectsTest() {
    List<SimplePlainObject> objs = factory
        .generator().metronome(50, MILLISECONDS)
        .predicate(countPredicate(10))
        .generateByClass(SimplePlainObject.class)
        .cache()
        .getValueList(SimplePlainObject.class);

    assertArrayEquals(
        " arrays from db and from cache must be equal",
        toArray(repository.findAll()),
        toArray(objs)
    );
  }


  private Object[] toArray(List<SimplePlainObject> objs) {
    return objs.stream().map(SimplePlainObject::getId).toArray();
  }
}

