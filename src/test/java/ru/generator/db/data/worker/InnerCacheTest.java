package ru.generator.db.data.worker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.generator.db.data.worker.data.SimplePlainObject;
import ru.generator.db.data.worker.data.SimplePlainObjectRepository;

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