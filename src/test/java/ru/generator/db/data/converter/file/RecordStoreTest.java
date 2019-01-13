package ru.generator.db.data.converter.file;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.generator.db.data.worker.MetaDataList;
import ru.generator.db.data.worker.data.hard.MainObject;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RecordStoreTest {
  @Autowired
  private MetaDataList list;


  @Test
  public void storeTest() throws Exception {
	URI uri = this.getClass().getClassLoader().getResource("test.csv").toURI();
	List<String> recs = Files.readAllLines(Paths.get(uri));
	FromFileStore store = FromFileStore.init(list,recs,new DummyStringTransformer());

	List<MainObject> mainObjects = store.find(MainObject.class);
	Assert.assertEquals(mainObjects.size(),1);

	MainObject mainObject = mainObjects.get(0);
	Assert.assertEquals(mainObject.getIdField(),1000);
	Assert.assertEquals(mainObject.getName(),"+++");
	Assert.assertEquals(mainObject.getBigDecimal(),new BigDecimal("1.00"));
	Assert.assertEquals(mainObject.getUuid(),UUID.fromString("8d64483c-6f83-40a9-9449-f0d8d736c49e"));
	Assert.assertEquals(1, (int) mainObject.getLazyObject().getId());
	Assert.assertEquals(2, (int) mainObject.getLazyObject2().getId());
	Assert.assertNull( mainObject.getLazyObjectAlwaysNull());

  }
}