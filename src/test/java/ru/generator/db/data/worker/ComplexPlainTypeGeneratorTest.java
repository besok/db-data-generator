package ru.generator.db.data.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.generator.db.data.worker.data.NakedObject;
import ru.gpb.utils.db.data.generator.worker.data.NakedObject;

import static org.junit.Assert.*;

/**
 * Created by Boris Zhguchev on 01/10/2018
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ComplexPlainTypeGeneratorTest {

  @Autowired
  private DatabaseDataGeneratorFactory factory;

  @Test
  public void test() {

    ComplexPlainTypeGenerator generator = new ComplexPlainTypeGenerator();
	generator.setPair(column -> column.getField().equals("field"), oldValue -> "field", String.class);
	NakedObject val =
	  factory
		.generator(generator)
		.generateBy(NakedObject.class)
		.cache()
		.getValueList(NakedObject.class)
		.get(0);

	assertEquals(val.getField(),"field");
  }
}