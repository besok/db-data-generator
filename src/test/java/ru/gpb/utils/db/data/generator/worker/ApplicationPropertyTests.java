package ru.gpb.utils.db.data.generator.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;

// 2018.07.24 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-wrong-arg-test.yml")
public class ApplicationPropertyTests {
  @Autowired
  private DatabaseDataGeneratorFactory factory;

  @Test(expected = IllegalArgumentException.class)
  public void testNullCacheSize() {
    factory
        .dummyGenerator()
        .generateByClass(SimplePlainObject.class); // expected IllegalArgumentException
  }

}

