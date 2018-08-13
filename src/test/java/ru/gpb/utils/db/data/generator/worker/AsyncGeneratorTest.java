package ru.gpb.utils.db.data.generator.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObjectRepository;

import static org.junit.Assert.*;

// 2018.08.13 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AsyncGeneratorTest {

  @Autowired
  private DatabaseDataGeneratorFactory factory;
  @Autowired
  private SimplePlainObjectRepository repository;
  @Test
  public void cache() {
    InnerCache cache = factory
        .generator()
        .repeate(50)
        .async()
        .generateBy(SimplePlainObject.class)
        .cache();
    System.out.println(cache);
  }
}