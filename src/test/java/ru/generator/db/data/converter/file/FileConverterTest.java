package ru.generator.db.data.converter.file;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.generator.db.data.converter.DbDataConverter;
import ru.generator.db.data.worker.data.hard.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Boris Zhguchev on 10/01/2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileConverterTest {


  @Autowired
  private DbDataConverter converter;

  @Autowired
  private MainObjectRepository mainObjectRepository;
  @Autowired
  private LazyObjectRepository lazyObjectRepository;

  @Before
  public void setUp() throws Exception {
	LazyObject lo = new LazyObject();
	lo.setId(10);
	lo.setName("lazy_object_1");
	lo.setADouble(1000.1000);
	lo.setCharacter('a');
	lazyObjectRepository.save(lo);
	MainObject mo = new MainObject();
	mo.setLazyObject(lo);
	mo.setLazyObject2(lo);
	mo.setName("+++");
	mo.setUuid(UUID.fromString("8d64483c-6f83-40a9-9449-f0d8d736c49e"));
	mo.setBigDecimal(BigDecimal.ONE);
	mo.setId(1000);
	mainObjectRepository.save(mo);
	NullLazyObject nlo = new NullLazyObject();
	nlo.setId(100);
	nlo.setMainObject(mo);
  }

  @Test
  public void commonTest() throws IOException, URISyntaxException {
	List<String> template = new ArrayList<>();
	template.add("@@@test.hard_object" );
	template.add("id;name;big_decimal;uuid;null_lazy_object_id;null_lazy_object_id_always_null;lazy_object_id" );
	template.add("1000;+++;1.00;8d64483c-6f83-40a9-9449-f0d8d736c49e;10;;10" );
	template.add("@@@test.lazy_object" );
	template.add("id;name;a_double;character;" );
	template.add("10;lazy_object_1;1000.1;a;" );

	URL resource = this.getClass().getClassLoader().getResource("\\");
	Path file = Paths.get(resource.toURI()).resolve("test.csv");
	Path path = converter.toFile(mainObjectRepository.getOne(1000), file);
	List<String> res = Files.readAllLines(path);
	Assert.assertEquals(res,template);
  }

}