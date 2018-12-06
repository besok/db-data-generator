package ru.generator.db.data.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import java.util.Set;

/**
 * Created by Boris Zhguchev on 06/12/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SboxTest {
  @PersistenceContext
  private EntityManager em;

  @Test
  public void sbox() {
	Set<EntityType<?>> entities = em.getMetamodel().getEntities();
  }
}
