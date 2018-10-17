package ru.generator.db.data.worker;
// 2018.07.24 

import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.logging.Logger;


/**
 * @author Boris Zhguchev
 * inner generator for db entity
 */
@SuppressWarnings("unchecked")
class DatabaseEntityGenerator {

  protected final InnerCache cache;
  private Logger LOGGER = Logger.getLogger(DatabaseDataGeneratorFactory.class.getName());
  private PlainTypeGenerator plainValueGenerator;
  private IdSequenceRuleGenerator seqGen;
  private Repositories repositories;

  DatabaseEntityGenerator(ApplicationContext context, InnerCache cache) {
	this.seqGen = new IdSequenceRuleGenerator(new IdSequenceGenerator(0), new HashMap<>());
	this.repositories = new Repositories(context);
	this.cache = cache;
  }

  public void setGenerator(PlainTypeGenerator generatorSupplier) {
	LOGGER.info("generator's been changed = " + generatorSupplier.getClass().getName());
	this.plainValueGenerator = generatorSupplier;
  }

  public void setStartSeq(long startSeq) {
	seqGen = new IdSequenceRuleGenerator(
	  new IdSequenceGenerator(startSeq),
	  seqGen.mapperMap
	);
  }

  protected Optional<Object> generateAndSaveSimpleObject(MetaData metaData) throws DataGenerationException {
	Class<?> aClass = metaData.getAClass();
	Object ent = null;
	try {
	  ent = aClass.newInstance();

	  MetaData.Id id = metaData.getId();
	  if (!id.isGenerated()) {
		Field idField = id.getIdField();
		idField.setAccessible(true);
		Object generatedId = seqGen.generate(idField.getType(), metaData.getAClass());
		idField.set(ent, generatedId);
	  }

	  for (MetaData.Column col : metaData.getPlainColumns()) {
		Field f = col.getVal();
		f.setAccessible(true);
		Object generatedObject = plainValueGenerator.generate(f.getType(), col);
		f.set(ent, generatedObject);
	  }

	} catch (InstantiationException | IllegalAccessException e) {
	  LOGGER.finest("exception's been caught: " + e.getClass().getSimpleName() + " for " + metaData.getAClass().getSimpleName());
	  throw new DataGenerationException("Reflection exception", e);
	}
	LOGGER.finest("plain object is being tried to save  = " + ent);

	return save(aClass, ent).map(cache(metaData));
  }


  protected Optional<Object> generateAndSaveObject(MetaData metaData) throws DataGenerationException {
	return generateAndSaveObject(metaData, new ArrayDeque<>());
  }


  protected Optional<Object> generateAndSaveObject(MetaData metaData, Deque<MetaData> path) throws DataGenerationException {
	if (metaData.isPlain())
	  return generateAndSaveSimpleObject(metaData);

	Class<?> aClass = metaData.getAClass();
	Object obj = null;
	try {
	  obj = aClass.newInstance();

	  for (Field f : aClass.getDeclaredFields()) {
		f.setAccessible(true);
		if (metaData.isId(f)) {
		  if (!metaData.getId().isGenerated()) {
			MetaData.Id id = metaData.getId();
			Field idField = id.getIdField();
			idField.setAccessible(true);
			Object generatedId = seqGen.generate(idField.getType(), metaData.getAClass());
			idField.set(obj, generatedId);
		  }
		} else {
		  MetaData before = metaData.dependency(f);
		  if (before == null) {
			Class<?> type = f.getType();
			Optional<MetaData.Column> column = metaData.findByField(f);
			if (column.isPresent()) {
			  // FIXME: 8/2/2018 Сделать обработку если это коллекция
			  Object generate = plainValueGenerator.generate(type, column.get());
			  f.set(obj, generate);
			} else {
			  if (metaData.neighbour(f) == null) {
				// FIXME: 10/17/2018 Define all cases for that.
				// -> not in neighbours,depends,plains
				// - optional = true
				// - it is not entity(enum)

			  }
			}
		  } else {

//		    in that section we have to check for case when a path is cycling.
//			cycle example: obj1 has field with obj2 and obj2 has field with obj3 and obj3 has field with obj1.
//			if we have a cycle we will take obj from cache or do nothing.

			if (path.stream().anyMatch(before::equals)) {
			  List<Object> beforeList = cache.getValueList(before);
			  if (!beforeList.isEmpty()) {
				f.set(obj, randomFromList(beforeList));
			  }

			} else {
			  path.addFirst(before);
			  Optional<Object> beforePojo = generateAndSaveObject(before, path);
			  if (beforePojo.isPresent()) {
				f.set(obj, beforePojo.get());
			  }
			}

		  }
		}
	  }
	} catch (InstantiationException | IllegalAccessException e) {
	  LOGGER.info("exception's been caught: " + e.getClass().getSimpleName() + " for " + metaData.getAClass().getSimpleName());
	  throw new DataGenerationException("Reflection exception ", e);
	}
	LOGGER.finest("plain object is being tried to save  = " + obj);
	return save(aClass, obj).map(cache(metaData));

  }


  private Object randomFromList(List<Object> list) {
	return list.get(new Random().nextInt(list.size()));
  }

  /**
   * method adds rules for processing specific fields or columns or class
   *
   * @param action    Action changing or modifying old generated value. @see {@link Action}
   * @param predicate condition for action. @see {@link ColumnPredicate}
   * @throws IllegalStateGeneratorException when invoking
   *                                        this method @see {@link Generator#rule(ColumnPredicate, Action, Class)} in custom generator
   *                                        which isn't inherited @see ComplexPlainTypeGenerator
   */
  public <V> void setPair(ColumnPredicate predicate, Action<V> action, Class<V> clzz) {
	if (plainValueGenerator instanceof ComplexPlainTypeGenerator) {
	  ((ComplexPlainTypeGenerator) plainValueGenerator).setPair(predicate, action, clzz);
	} else {
	  throw
		new IllegalStateGeneratorException(" the method rule can be invoked only " +
		  "with a class ComplexPlainTypeGenerator or it's childs.");
	}
  }

  /**
   * method adds rules for processing id for specific class.
   *
   * @param action    Action changing or modifying old generated value. @see {@link Action}
   * @param pojoClass pojo class. @see {@link ColumnPredicate}
   */
  public <V> void setPairForId(Class<?> pojoClass, Action<V> action) {
	seqGen.setPair(pojoClass, action);
  }


  private Function<Object, Object> cache(MetaData metaData) {
	return c -> {
	  cache.put(metaData, c);
	  return c;
	};
  }

  private Optional<Object> save(Class<?> aClass, Object ent) {
	return repositories.getRepositoryFor(aClass).map(o -> ((JpaRepository) o).save(ent));
  }


  // generate id
  private class IdSequenceGenerator extends AbstractPlainTypeGenerator {


	private AtomicLong seq;

	public IdSequenceGenerator(long seqStart) {
	  this.seq = new AtomicLong(seqStart);
	}

	@Override
	public Function<MetaData.Column, UUID> uuid() {
	  return unpack(UUID.randomUUID());
	}

	@Override
	public Function<MetaData.Column, BigDecimal> bigDecimal() {
	  return unpack(BigDecimal.valueOf(seq.incrementAndGet()));
	}

	@Override
	public Function<MetaData.Column, Integer> integerVal() {
	  return unpack(Math.toIntExact(seq.incrementAndGet()));
	}

	@Override
	public Function<MetaData.Column, Double> doubleVal() {
	  return unpack((double) seq.incrementAndGet());
	}

	@Override
	public Function<MetaData.Column, Long> longVal() {
	  return unpack(seq.incrementAndGet());
	}
  }

  private class IdSequenceRuleGenerator {
	protected Map<String, List<Action<Object>>> mapperMap;
	private PlainTypeGenerator delegate;

	IdSequenceRuleGenerator(PlainTypeGenerator delegate,
							Map<String, List<Action<Object>>> mapperMap) {

	  this.delegate = delegate;
	  this.mapperMap = mapperMap;
	}

	public <V> void setPair(Class<?> pojoClass, Action<V> action) {
	  mapperMap.compute(pojoClass.getName(),
		(k, v) -> {
		  if (v == null)
			v = new ArrayList<>();
		  v.add((Action<Object>) action);
		  return v;
		});
	}

	public Object generate(Class<?> clazz, Class<?> pojoClass) {
	  Object generate = delegate.generate(clazz, null);
	  List<Action<Object>> actions = mapperMap.getOrDefault(pojoClass.getName(), new ArrayList<>());
	  for (Action<Object> action : actions) {
		generate = action.process(generate);
	  }

	  return generate;
	}
  }
}
