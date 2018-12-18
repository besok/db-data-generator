package ru.generator.db.data.worker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.generator.db.data.worker.data.*;

import java.util.List;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.*;

// 2018.07.24 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class CommonSimpleObjectTest {


  @Autowired
  private DatabaseDataGeneratorFactory factory;
  @Autowired
  private SimplePlainObjectRepository repository;
  @Autowired
  private ComplexObjectRepository complexObjectRepository;

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
        .success();

    assertEquals(marker , repository.findAll().size());
  }

  @Test
  @Repeat(5)
  public void generatePlainObjectsTest() {
    List<SimplePlainObject> objs = factory
        .dummyGenerator()
        .metronome(50, MILLISECONDS, MetronomePredicate.COUNT(10))
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
        .repeate(5)
        .generateBy(SimplePlaiObjectGenId.class)
        .cache()
        .getValueList(SimplePlaiObjectGenId.class)
        .stream()
        .map(SimplePlaiObjectGenId::getId)
        .max(Integer::compareTo)
        .get();

    assertEquals("expected = " + expectedId + "==15", 15, (int) expectedId);
  }
  @Test
  public void asyncTest(){
    factory
        .generator().async()
        .repeate(100)
        .generateBy(SimplePlainObject.class)
        .finish();

   assertEquals(100,repository.findAll().size());

  }
  @Test
  public void hugeLogFailedTest(){
    factory
        .generator()
        .repeate(100_000)
        .generateBy(SimplePlainObject.class)
        .finish();

  }

  @Test
  public void simpleComplexTest(){
    Generator generator = factory
        .generator()
        .repeate(500)
        .generateBy(ComplexObject.class)
        .finish();

    assertEquals(500,complexObjectRepository.findAll().size());
	int id = generator.cache().getValueList(ComplexObject.class).get(0).getId();
	Optional<ComplexObject> co = complexObjectRepository.findById(id);
	assertTrue(co.isPresent());

	SimplePlainObject2 plain = co.get().getPlain();
	assertNotNull(plain);
  }

  private Object[] toArray(List<SimplePlainObject> objs) {
    return objs.stream().map(SimplePlainObject::getId).sorted(Integer::compareTo).toArray();
  }
}

