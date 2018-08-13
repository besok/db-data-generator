package ru.gpb.utils.db.data.generator.worker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObjectRepository;

import java.util.List;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.*;
import static ru.gpb.utils.db.data.generator.worker.MetronomeGenerator.MetronomePredicate.*;

// 2018.07.24 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SimpleCommonTests {


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
        .generateBy(SimplePlainObject.class)
        .log()
        .markerValue();

    assertEquals(marker / 2, repository.findAll().size());
  }

  @Test
  @Repeat(5)
  public void generatePlainObjectsTest() {
    List<SimplePlainObject> objs = factory
        .dummyGenerator()
        .metronome(50, MILLISECONDS, COUNT(10))
        .generateBy(SimplePlainObject.class)
        .cache()
        .getValueList(SimplePlainObject.class);

    assertArrayEquals(
        " arrays from db and from cache must be equal",
        toArray(repository.findAll()),
        toArray(objs)
    );
  }

  @Test
  public void ldtTest() {
    List<SimplePlainObject> valueList = factory
        .generator()
        .generateBy(SimplePlainObject.class)
        .cache().getValueList(SimplePlainObject.class);
    assertEquals(1, valueList.size());
    assertNotNull(valueList.get(0).getLdt());
  }


  @Test
  public void cacheSizeTest() {

    int s1 = factory
        .dummyGenerator()
        .repeate(20)
        .generateBy(SimplePlainObject.class)
        .cache().getValueList(SimplePlainObject.class).size();
    assertEquals(20, s1);

    int s2 = factory
        .dummyGenerator()
        .repeate(21)
        .generateBy(SimplePlainObject.class)
        .cache().getValueList(SimplePlainObject.class).size();

    assertTrue(21 > s2);
  }

  @Test
  public void idSeqTest() {

    Integer expectedId= factory
        .generator()
        .startId(10)
        .repeate(10)
        .generateBy(SimplePlainObject.class)
        .cache().getValueList(SimplePlainObject.class)
        .stream().map(SimplePlainObject::getId).max(Integer::compareTo).get();

    assertEquals("expected = " + expectedId + "==20", 20, (int) expectedId);
  }
  @Test
  public void asyncTest(){
    InnerCache cache = factory
        .generator().async()
        .repeate(100)
        .generateBy(SimplePlainObject.class)
        .cache();

    System.out.println(cache.getValueList(SimplePlainObject.class));
    System.out.println(repository.findAll().size());

  }
  private Object[] toArray(List<SimplePlainObject> objs) {
    return objs.stream().map(SimplePlainObject::getId).sorted(Integer::compareTo).toArray();
  }
}

