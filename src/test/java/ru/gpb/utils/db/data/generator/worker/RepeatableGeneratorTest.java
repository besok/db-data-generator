package ru.gpb.utils.db.data.generator.worker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.ComplexObject;
import ru.gpb.utils.db.data.generator.worker.data.ComplexObjectRepository;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObjectRepository;

import static org.junit.Assert.*;

// 2018.08.05 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RepeatableGeneratorTest {
  @Autowired
  DatabaseDataGeneratorFactory factory;
  @Autowired
  SimplePlainObjectRepository simplePlainObjectRepository;
  @Autowired
  ComplexObjectRepository complexObjectRepository;


  @Before
  public void setUp() throws Exception {
    complexObjectRepository.deleteAll();
  }

  @Test
  public void repeateSimple() {
    factory.generator()
        .repeate(5)
        .generateBy(SimplePlainObject.class);

    int size = simplePlainObjectRepository.findAll().size();
    assertEquals(5, size);
  }

  @Test
  public void repeateComplex() {
    factory.generator()
        .repeate(5)
        .generateBy(ComplexObject.class);

    int size = complexObjectRepository.findAll().size();
    assertEquals(5, size);
  }
  @Test
  public void repeateComplexDuplicate() {
    factory.generator()
        .repeate(5)
        .repeate(5)
        .generateBy(ComplexObject.class);

    int size = complexObjectRepository.findAll().size();
    assertEquals(5, size);
  }

  @Test
  public void repeateComplexByTable() {
    factory.generator()
        .repeate(5)
        .generateBy("test","complex_object");

    int size = complexObjectRepository.findAll().size();
    assertEquals(5, size);
  }
}