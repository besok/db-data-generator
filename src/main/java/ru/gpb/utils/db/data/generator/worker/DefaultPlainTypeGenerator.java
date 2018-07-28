package ru.gpb.utils.db.data.generator.worker;
// 2018.07.26 

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import java.util.function.Function;

/**
 * @author Boris Zhguchev
 */
public class DefaultPlainTypeGenerator extends AbstractPlainTypeGeneratorSupplier {

  private Random random =new Random(1000);
  private Long counter = 0l;


  @Override
  public Function<MetaData, String> string() {
    return p -> p.getAClass().getSimpleName()+"-"+counter++;
  }

  @Override
  public Function<MetaData, BigDecimal> bigDecimal() {
    return unpack(BigDecimal.valueOf(random.nextInt()));
  }

  @Override
  public Function<MetaData, Integer> integer() {
    return unpack(random.nextInt()*10);
  }

  @Override
  public Function<MetaData, Double> doubleVal() {
    return unpack(random.nextDouble());
  }

  @Override
  public Function<MetaData, Date> date() {
    return unpack(new Date());
  }

  @Override
  public Function<MetaData, Timestamp> timestamp() {
    return unpack(new Timestamp(new Date().getTime()));
  }


  @Override
  public Function<MetaData, byte[]> bytes() {
    return p -> (p.toString() + "+"+random.nextLong()).getBytes();
  }

  private <R>Function<MetaData, R> unpack(R r){
    return p->r;
  }
}
