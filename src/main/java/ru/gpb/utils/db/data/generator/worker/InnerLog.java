package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;

/**
 *
 * Inner logger.
 * It collects all results and can gathering them to string.
 * Also it has {@link InnerLog#success} and @link {@link InnerLog#failure} which represents COUNT state for this log.
 *
 * @author Boris Zhguchev
 */
public class InnerLog {
  private final AtomicLong success;
  private final AtomicLong failure;
  private final String ent;


  public InnerLog(String ent) {
    this.ent=ent;
    success=new AtomicLong(0);
    failure=new AtomicLong(0);
  }
  public InnerLog(InnerLog log) {
    this.ent=log.ent;
    success=log.success;
    failure=log.failure;
  }

  public void successInc(){
    success.incrementAndGet();
  }
  public void failureInc(){
    failure.incrementAndGet();
  }

  public long marker(){return success()+failure();}
  public long success(){
    return success.longValue();
  }
  public long failure(){
    return failure.longValue();
  }

  public String toString() {
    return "Entity["+ent+"] => success["+success.longValue()+"] failure["+failure.longValue()+"]";
  }



}
