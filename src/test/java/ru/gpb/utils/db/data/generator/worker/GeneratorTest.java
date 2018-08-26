package ru.gpb.utils.db.data.generator.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;

import static org.junit.Assert.*;

// 2018.08.05 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GeneratorTest {

  @Autowired
  private DatabaseDataGeneratorFactory factory;

  @Test
  public void contextSavesSequenceGeneratorCall() {
    String report = factory
        .generator()
        .repeate(1)
        .repeate(1)
        .generateBy(SimplePlainObject.class)
        .report();

    System.out.println(report);

  }
}