package ru.generator.db.data.converter.file;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.generator.db.data.worker.MetaDataList;
import ru.generator.db.data.worker.data.hard.MainObject;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RecordStoreTest {
  @Autowired
  private MetaDataList list;


  @Test
  public void splitRaw() throws Exception {
	URI uri = this.getClass().getClassLoader().getResource("test.csv").toURI();
	List<String> recs = Files.readAllLines(Paths.get(uri));
	RecordStore store = RecordStore.init(list,recs,new DummyStringTransformer());

	List<MainObject> mainObjects = store.find(MainObject.class);
	System.out.println(mainObjects);
  }
}