package ru.generator.db.data.converter.file;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.generator.db.data.worker.MetaDataList;
import ru.generator.db.data.worker.data.ComplexObject;
import ru.generator.db.data.worker.data.SimplePlainObject2;

import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * Created by Boris Zhguchev on 10/01/2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileConverterTest {

  @Autowired
  private MetaDataList metaDataList;


  @Test
  public void colHeader() {

    ComplexObject obj = new ComplexObject();
    obj.setId(1);
    obj.setName("name");
    obj.setName1("name1");
    obj.setPlain(new SimplePlainObject2());

	Path sample = new FileConverter<ComplexObject>(metaDataList)
	  .withName("sample")
	  .to(obj);
  }
}