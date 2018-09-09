package ru.gpb.utils.db.data.generator.worker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObjectRepository;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

// 2018.08.05 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class InnerCacheTest {
  @Autowired
  DatabaseDataGeneratorFactory factory;
  @Autowired
  SimplePlainObjectRepository spoRepo;



  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void snapshot() {
    Map<MetaData, Integer> snapshot = factory.generator()
        .repeate(10)
        .generateBy(SimplePlainObject.class)
        .cache().snapshot();

    for (Map.Entry<MetaData, Integer> entry : snapshot.entrySet()) {
      if(entry.getKey().getAClass().equals(SimplePlainObject.class)){
        assertEquals(entry.getValue().intValue(),10);
      }
    }

  }

  @Test
  public void valueList(){
    List<SimplePlainObject> fromCache = factory
        .generator()
        .repeate(10)
        .generateBy(SimplePlainObject.class)
        .cache().getValueList(SimplePlainObject.class);

    List<SimplePlainObject> fromDB = spoRepo.findAll();

    Object[] fromCacheArr = fromCache.stream().map(SimplePlainObject::getName).sorted().toArray();
    Object[] fromDBArr = fromDB.stream().map(SimplePlainObject::getName).sorted().toArray();

    assertArrayEquals(fromDBArr,fromCacheArr);
  }
}