package ru.gpb.utils.db.data.generator.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.ComplexObject;
import ru.gpb.utils.db.data.generator.worker.data.NakedObject;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;

import java.math.BigDecimal;
import java.util.function.Function;

import static org.junit.Assert.*;
import static ru.gpb.utils.db.data.generator.worker.Action.*;
import static ru.gpb.utils.db.data.generator.worker.ColumnPredicate.*;

// 2018.08.05 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GeneratorTest {

  @Autowired
  private DatabaseDataGeneratorFactory factory;

  // TODO: 8/6/2018 Если сначала metronome а потом  repeate то в report, и т.д. теряются все предыдущие операции. Нужен CTX
  @Test
  public void infoGetLastGeneratorOnly() {
	String report = factory
	  .generator()
	  .repeate(1)
	  .repeate(1)
	  .generateBy(SimplePlainObject.class)
	  .report();

	System.out.println(report);
  }

  @Test
  public void startRuleComposeCheckTest() {
	NakedObject spo = factory
	  .generator().repeate(10)
	  .rule(COMPOSE(CLASS(NakedObject.class), FIELD("fieldWithCamel")), oldValue -> "!!!", String.class)
	  .generateBy(NakedObject.class)
	  .cache().getValueList(NakedObject.class).get(9);

	assertNotEquals("!!!", spo.getField());
	assertNotEquals("!!!", spo.getFieldWithCamelCase());
	assertEquals("!!!", spo.getFieldWithCamel());
  }

  @Test
  public void startRuleComposeCheckTrickTest() {
	SimplePlainObject spo = factory
	  .generator()
	  .rule(CLASS(SimplePlainObject.class), CONST(new Object()), Object.class)
	  .generateBy(SimplePlainObject.class)
	  .cache().getValueList(SimplePlainObject.class).get(0);
  }

  @Test(expected = IllegalStateGeneratorException.class)
  public void ruleForNotComplexGenTestFailed() {
	factory
	  .generator(new AbstractPlainTypeGenerator() {})
	  .rule(FIELD("FAKE"), OLD(), Object.class)
	  .generateBy(NakedObject.class)
	;

  }

}