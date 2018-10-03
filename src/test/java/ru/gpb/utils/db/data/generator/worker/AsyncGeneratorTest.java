package ru.gpb.utils.db.data.generator.worker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject2;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObjectRepository;

import java.util.Map;

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
  @Repeat(50)
  public void cache() {
    factory
        .generator()
        .async()
        .repeate(50)
        .generateBy(SimplePlainObject.class)
        .finish()
    ;


    assertEquals(50, repository.findAll().size());
  }


  // FIXME: 8/13/2018 Если запустить асинк на 1 таблицу 2 раза генерайтедбай, возникает гонка - меньше 100(id generator)
  @Test
  public void raceConditionToDB() {
    factory
        .generator()
        .async().repeate(100)
        .generateBy(SimplePlainObject.class)
        .generateBy(SimplePlainObject2.class)
        .log().marker();
    assertEquals(200, repository.findAll().size());
  }

  // FIXME: 9/2/2018 log/report возвращают последнюю запись вместо всех если generateBy два класса
  @Test
  public void logOrReportWithDupAreCorrect() {
    int length =
        factory
            .generator()
            .async().repeate(100)
            .generateBy(SimplePlainObject.class)
            .generateBy(SimplePlainObject.class) // дубли
            .report()
            .split(System.lineSeparator()).length;

    assertEquals(2, length);
  }

  @Before
  public void setUp() throws Exception {
    repository.deleteAll();
  }
}