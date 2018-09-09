package ru.gpb.utils.db.data.generator.worker;

import org.junit.Test;

import static org.junit.Assert.*;

// 2018.08.05 

/**
 * @author Boris Zhguchev
 */
public class InnerLogTest {

  @Test
  public void increment() {
    InnerLog log = new InnerLog("entity");
    log.successInc();
    log.failureInc();
    assertEquals(log.success(),1);
    assertEquals(log.failure(),1);
    assertEquals(log.marker(),2);
    assertEquals("Entity[entity] => success[1] failure[1]",log.toString());

  }
}