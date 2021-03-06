package ru.generator.db.data.worker;
// 2018.07.24 

import java.util.concurrent.atomic.AtomicLong;

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

  void successInc(){
    success.incrementAndGet();
  }
  void failureInc(){
    failure.incrementAndGet();
  }

  /**
   * @return effort count
   * */
  public long marker(){return success()+failure();}

  /**
   * @return succes count
   * */
  public long success(){ return success.longValue(); }


  /**
   * @return failure count
   * */
  public long failure(){
    return failure.longValue();
  }

  public String toString() {
    return "Entity["+ent+"] => success["+success.longValue()+"] failure["+failure.longValue()+"]";
  }



}
