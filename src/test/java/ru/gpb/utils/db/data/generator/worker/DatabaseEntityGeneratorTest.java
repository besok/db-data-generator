package ru.gpb.utils.db.data.generator.worker;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObjectRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

// 2018.08.05 

/**
 * @author Boris Zhguchev
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DatabaseEntityGeneratorTest {

   DatabaseEntityGenerator dbEntGen;

  @Autowired
  MetaDataList mdl;
  @Autowired
  SimplePlainObjectRepository simpleRepo;
  @Autowired
  ApplicationContext context;
  @Autowired
  InnerCache cache;

  @Before
  public void prepare(){
    dbEntGen = new DatabaseEntityGenerator(context,cache);
    dbEntGen.setGenerator(new DefaultPlainTypeGenerator());
    simpleRepo.deleteAll();
  }

  @Test
  public void generateAndSaveSimpleObject() throws DataGenerationException {
    Optional<MetaData> metaData = mdl.byClass(SimplePlainObject.class);
    assertNotNull(metaData.get());

    Optional<Object> gen = dbEntGen.generateAndSaveSimpleObject(metaData.get());
    assertNotNull(gen.get());

    List<SimplePlainObject> objs = simpleRepo.findAll();
    assertEquals(1,objs.size());

    List<SimplePlainObject> vl = dbEntGen.cache.getValueList(SimplePlainObject.class);
    assertEquals(1,vl.size());
    assertEquals(vl.get(0).getId(),objs.get(0).getId());

  }

}