### Introduction:
Database data generator.It based on Spring Data Repository.

### Work stages:
* Scan all JpaRepository beans. Get Entity classes. 
* Store meta data from @Table, @ManyToMany,@OneToMany,@OneToOne,@ManyToOne and etc .
* Traversal all entities and construct relations(dependents and neighbours).
* Generate plain instances without dependencies.
* Generate parent instances having @OneToOne(optional=true),@OneToMany and etc
* Generate child instances having parent object as dependency(usual haves a column with parent id)
    * Put generated objects to cache if the cache is empty. 
* Generate neighbour relations based on cache. It's a ManyToMany link.


### Properties:
* set default jpa properties - @EnableJpaRepositories, @EntityScan and etc
* should set **@EnableDatabaseDataGenerator**
* for Tables with One To One relations should field optional=true to be exist for one of that tables 
* add gradle dependency  
```
compile group: 'ru.gpb.als.source.generator', name: 'db-data-generator', version: '0.1'
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
* ? extends Generator - common class , which generates common logic.
* InnerLog - log entity, which contains all activity.
* InnerCache - inner cache with generated objects.It can be used for getting generated objects without an db impact.  
* MetaData - inner data class which has all parsed information from JpaRepository.
* AbstractPlainTypeGeneratorSupplier - class describes a method for generation plain types(integer, long, string and etc). 
    * It takes Metadata instance for custom generation.


### Notes:
* if a field hasn't an annotation @Column with column name the generator takes column 
name from field name converting camel case to snake case.

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
            .generator().metronome(1, TimeUnit.SECONDS,ctx -> ctx.log.markerValue() < 10)
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
          .rule(COMPOSE(CLASS(NakedObject.class), FIELD("fieldWithCamel")), CONST("newValue"), String.class)
          .generateBy(NakedObject.class)
          .generateBy(NakedObject2.class)
          .generateBy(NakedObject3.class)
    	
```


