package ru.generator.db.data.worker;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.generator.db.data.worker.data.joinprimarykey.OneToOneLeft;
import ru.generator.db.data.worker.data.joinprimarykey.OneToOneLeftRepository;
import ru.generator.db.data.worker.data.joinprimarykey.OneToOneRight;
import ru.generator.db.data.worker.data.joinprimarykey.OneToOneRightRepository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Boris Zhguchev on 19/12/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableTransactionManagement
public class JoinPrimaryColumnTest {

  @Autowired
  private DatabaseDataGeneratorFactory factory;
  @Autowired
  private OneToOneLeftRepository leftRepo;
  @Autowired
  private OneToOneRightRepository rightRepo;

  @Test
  @Transactional
  public void withoutGeneratorTest() throws DataGenerationException {
	OneToOneLeft left = new OneToOneLeft();
	left.setId(UUID.randomUUID());
	leftRepo.save(left);
	OneToOneRight right = new OneToOneRight();
	right.setId(left.getId());
	right.setEnt(left);
	left.setEnt(right);
	rightRepo.save(right);
	leftRepo.save(left);
	List<OneToOneLeft> l = leftRepo.findAll();
	List<OneToOneRight> r = rightRepo.findAll();

	Assert.assertEquals(1, l.size());
	Assert.assertEquals(1, r.size());
	Assert.assertEquals(l.get(0).getEnt(), r.get(0));
	Assert.assertEquals(l.get(0), r.get(0).getEnt());

  }

  @Test
  @Transactional
  public void withGeneratorTest() throws DataGenerationException {
	InnerCache cache = factory.generator()
	  .generateBy(OneToOneRight.class)
	  .withException()
	  .cache();

	List<OneToOneLeft> leftAll = leftRepo.findAll();
	List<OneToOneRight> rightAll = rightRepo.findAll();

	Assert.assertEquals(leftAll.size(), rightAll.size());
	Assert.assertEquals(leftAll.get(0).getEnt(), rightAll.get(0));
	Assert.assertEquals(leftAll.get(0), rightAll.get(0).getEnt());
  }
}
