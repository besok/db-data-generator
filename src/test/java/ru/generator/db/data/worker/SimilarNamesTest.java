package ru.generator.db.data.worker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.generator.db.data.worker.data.similar.ForSimilarObject;
import ru.generator.db.data.worker.data.similar.p1.SimilarObject;
import ru.generator.db.data.worker.data.similar.p2.SimilarObjectRepository;

/**
 * Created by Boris Zhguchev on 16/10/2018
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SimilarNamesTest {

  @Autowired
  private DatabaseDataGeneratorFactory factory;




  // FIXME: 10/16/2018 Одинаковые репозитории не сохраняются -> Spring Repositories
  // Дело в том, что спринг использует простое  имя класса для создания бина,
  // поэтому бины с одинаковыми именами не сохраняются.
  // для решения добавляется qualifier или @repository(beanName).
  // Основная задача - попробовать на этапе создания увидеть что это дубль и выкинуть ворнинг.
  @Test
  public void similarNameTest() throws DataGenerationException {
	factory
	  .generator()
	  .generateBy(ForSimilarObject.class)
	  .withException();
  }


}
