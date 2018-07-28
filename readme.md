#### Description:
Database data generator.It based on Spring Data Repository.

#### Work stages:
...


#### Properties:
* set default jpa properties - @EnableJpaRepositories, @EntityScan and etc
* should set **@EnableDatabaseDataGenerator**
* for Tables with One To One relations should field optional=true to be exist for one of that tables 
* add gradle dependency  
```
compile group: 'ru.gpb.als.source.generator', name: 'db-data-generator', version: '0.1'
```
* (optional) add property *generator.cache-entity-size* to your application file. 
    * It manages generated size between many2many relations. By default it is 20.
    * For example, we have 2 tables A and B with m2m rel. Generator takes 20 entities from each table and generate 400 relations each other.

#### Usage examples:

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
                .generateByClass(Limit.class)
                .generateByClass(Customer.class)
                .generateByClass(Currency.class)
                .generateByClass(OrganizationUnit.class)
                .report();

```
##### Generate by Table
```
       String report =
            factory
                .generator()
                .generateByTable("schema","table")
                .report();

```

##### Generate by Table with exception
```

    try {
      InnerLog log =
          factory
              .generator()
              .generateByTable("schema", "table")
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
              .generateByClass(Limit.class)
              .log;

```
##### Generate by Class and do it till exprected log size  
```
    InnerLog s =
        factory
            .generator().metronome(1, TimeUnit.SECONDS)
            .predicate(countPredicate(10))
            .generateByClass(Customer.class)
            .log();

    InnerLog s =
        factory
            .generator().metronome(1, TimeUnit.SECONDS)
            .predicate(ctx -> ctx.log.markerValue() < 10)
            .generateByClass(Customer.class)
            .log();
```