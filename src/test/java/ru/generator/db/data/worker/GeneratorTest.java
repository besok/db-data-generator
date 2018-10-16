package ru.generator.db.data.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.generator.db.data.worker.data.NakedObject;
import ru.generator.db.data.worker.data.SeqIncObject;
import ru.generator.db.data.worker.data.SimplePlaiObjectGenId;
import ru.generator.db.data.worker.data.SimplePlainObject;
import ru.gpb.utils.db.data.generator.worker.data.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	String report =
	  factory.generator()
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
	  .rule(CLASS(SimplePlainObject.class), Action.CONST(new Object()), Object.class)
	  .generateBy(SimplePlainObject.class)
	  .cache().getValueList(SimplePlainObject.class).get(0);
  }


  /**
   * This test checks the generator
   * has been invoked by default @see {@link DatabaseDataGeneratorFactory#generator()}
   * or inherited from {@link ComplexPlainTypeGenerator}.
   * In other case it will throw IllegalStateGeneratorException
   **/
  @Test(expected = IllegalStateGeneratorException.class)
  public void ruleForNotComplexGenTestFailed() {
	factory
	  .generator(new AbstractPlainTypeGenerator() {
	  })
	  .rule(FIELD("FAKE"), Action.OLD(), Object.class)
	  .generateBy(NakedObject.class)
	;

  }

  @Test
  public void affectlessSameTypeFieldTest() {

    List<Integer> randomVal = new ArrayList<>();

	List<SeqIncObject> list =
	  factory.generator()
		.repeate(10)
		.rule(FIELD("iLeft"), Action.INCREMENT_I(0), int.class)
		.rule(FIELD("iRight"), Action.INCREMENT_I(0), int.class)
		.rule(FIELD("lLeft"), Action.INCREMENT_L(0), Long.class)
		.rule(FIELD("lRight"), Action.INCREMENT_L(0), long.class)
		.rule(FIELD("random"), Action.RANDOM(10), int.class)
		.rule(FIELD("random"), Action.PEEK(randomVal::add), int.class)
		.generateBy(SeqIncObject.class)
		.cache()
		.getValueList(SeqIncObject.class);

	SeqIncObject last = list.get(9);
	assertEquals(10, last.getILeft());
	assertEquals(10, last.getIRight());
	assertEquals(10, (long) last.getLLeft());
	assertEquals(10, last.getLRight());
	assertEquals(10,randomVal.size());

	int i = 0;
	for (SeqIncObject object : list) {
	  assertTrue(object.getRandom() < 10);
	  assertTrue(randomVal.get(i++) < 10);
	}

  }


  @Test
  public void localDateTest() {
	SimplePlaiObjectGenId obj = factory.generator()
	  .generateBy(SimplePlaiObjectGenId.class)
	  .cache()
	  .getValueList(SimplePlaiObjectGenId.class).get(0);

	assertEquals(LocalDate.now(), obj.getLd());
  }

  @Test
  public void setRuleForIdTest() {
	List<SimplePlaiObjectGenId> id = factory
	  .generator()
	  .repeate(10)
	  .ruleId(SimplePlaiObjectGenId.class, Action.CONST(1))
	  .generateBy(SimplePlaiObjectGenId.class)
	  .cache()
	  .getValueList(SimplePlaiObjectGenId.class);

	assertEquals(id.get(10).getId(),1);
  }

  @Test
  public void shortNameTest() {
	List<String> vl = factory
	  .generator()
	  .repeate(10)
	  .generateBy(SimplePlainObject.class)
	  .cache()
	  .getValueList(SimplePlainObject.class)
	  .stream()
	  .map(SimplePlainObject::getShortName)
	  .distinct().collect(Collectors.toList());

	assertTrue(vl.size() > 1);
	assertEquals(1, vl.stream().mapToInt(String::length).max().getAsInt());

  }
}