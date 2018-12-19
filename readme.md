### Introduction:
Database data generator based on Spring Data Repository. 
This tool can be used in your spring data project for generating some data.

### Requirements
* spring data module
* spring data repos
* jpa entities

### Work stages:
* Find all JpaRepository beans through @EnableJpaRepositories, @EntityScan.Get entity classes. 
* Store metadata from @Table, @ManyToMany,@OneToMany,@OneToOne,@ManyToOne and etc .
* Traversal all entities and construct relations(dependents and neighbours) each other.
* Generate plain instances for entities without dependencies.
* Generate parent instances having @OneToOne(optional=true),@OneToMany and etc
* Generate child instances having parent object as dependency(usual haves a column with parent id)
    * Put generated objects to cache if the cache is empty. 
* Generate neighbour relations(ManyToMany link).

### Properties:
* set default jpa properties - @EnableJpaRepositories, @EntityScan and etc
* should set **@EnableDatabaseDataGenerator**
* Tables with @OneToOne relations should field optional=true to be exist for one of that tables 
* Tables with @oneToOne relations and @PrimaryKeyJoinColumn should have this annotation only one for this relation
* add gradle dependency from your local repo :)  
```
compile group: 'ru.generator.db.data', name: 'db-data-generator', version: '0.2'
```
* (optional) add property *generator.cache-entity-size* to your application file. 
    * It manages generated size between many2many relations. By default it is 20.
      *For example, we have 2 tables A and B with m2m rel. Generator takes 20 entities from each table and generate 400 relations each other.*
* if you have id fields without @GeneratedValue generator will use sequence for numeric and random for uuid. For set start sequence value , using 
```
    generator.startId() // 0 by default
```
### Subjects:
* DatabaseDataGeneratorFactory - generator factory.
* <? extends Generator> - common class generates common logic.
* InnerLog - log entity contains inner log stats.
* InnerCache - inner cache with generated objects.It can be used for getting generated objects without an db impact.  
* MetaData - inner data class has all parsed information from JpaRepository.
* PlainTypeGenerator - interface describes a method for generation plain types(integer, long, string and etc). 
    * It takes Metadata instance for custom generation.
* Action - SPA for customizing generated value
* ColumnPredicate - Spa for filtering rules.

### Notes:
* if a field hasn't an annotation @Column with column name the generator takes column 
name from field name converting camel case to snake case.
* if objects has a cycle(obj1 has obj2 field then obj2 has obj3 field then obj3 has obj1 field) 
one of that columns must be optional.

### Usage examples:

##### Get the factory:
```
     @Autowired
     private DatabaseDataGeneratorFactory factory;
     
```
##### Generate all values

```
    String report =
            factory
                .generator()
                .generateAll()
                .report();

```

##### Generate by Class
```
    String report =
            factory
                .generator()
                .generateBy(Limit.class)
                .generateBy(Customer.class)
                .generateBy(Currency.class)
                .generateBy(OrganizationUnit.class)
                .report();

```
##### Generate by Table
```
       String report =
            factory
                .generator()
                .generateBy("schema","table")
                .report();

```

##### Generate by Table with exception
```

    try {
      InnerLog log =
          factory
              .generator()
              .generateBy("schema", "table")
              .withException()
              .log;
    } catch (DataGenerationException e) {
      e.printStackTrace();
    }

```

##### Generate by Class and repeate it 10 times
```
 InnerLog log =
          factory
              .generator().repeate(10)
              .generateBy(Limit.class)
              .log;

```
##### Generate by Class and do it till exprected log size  
```
    InnerLog s =
        factory
            .generator().metronome(1, TimeUnit.SECONDS)
            .predicate(countPredicate(10))
            .generateBy(Customer.class)
            .log();

    InnerLog s =
        factory
            .generator()
            .metronome(1, TimeUnit.SECONDS,ctx -> ctx.log.markerValue() < 10)
            .generateBy(Customer.class)
            .log();
```
##### Generate by Class and get cache 
```
    InnerCache cache = factory
        .generator()
        .metronome(10,TimeUnit.MILLISECONDS,COUNT(10))
        .generateBy(SimplePlainObject.class)
        .cache();
```

##### Generate by Class and do it async
```
    factory
        .generator().async()
        .repeate(100)
        .generateBy(SimplePlainObject1.class)
        .generateBy(SimplePlainObject2.class)
        .generateBy(SimplePlainObject3.class)
        .finish();
```
##### Add specific logic for generated value - rule(ColumnPredicate, Action, Class<?>)
```
    import static ru.gpb.utils.db.data.generator.worker.Action.*;
    import static ru.gpb.utils.db.data.generator.worker.ColumnPredicate.*;
    ... 
    
    NakedObject spo = factory
    	  .generator()
    	  .rule(FIELD("fieldWithCamel"), CONST("newValue"), String.class)
    	  .generateBy(NakedObject.class)
    	  .cache()
    	  .getValueList(NakedObject.class)
    	  .get(0);
    
    	assertEquals("newValue", spo.getFieldWithCamel());
    	
    NakedObject spo = factory
          .generator()
          .rule(
            COMPOSE(CLASS(NakedObject.class), FIELD("fieldWithCamel")), 
            CONST("newValue"), String.class)
          .generateBy(NakedObject.class)
          .generateBy(NakedObject2.class)
          .generateBy(NakedObject3.class)
          
    List<SeqIncObject> list =
    	  factory.generator()
    		.repeate(10)
    		.rule(FIELD("lRight"), INCREMENT_L(0), long.class)
    		.rule(FIELD("random"), RANDOM(10), int.class)
    		.rule(FIELD("random"), PEEK(System.out::println), int.class)
    		.generateBy(SeqIncObject.class)
    		.cache()
    		.getValueList(SeqIncObject.class);      
    	
```

### License
This project is licensed under the terms of the MIT license.
