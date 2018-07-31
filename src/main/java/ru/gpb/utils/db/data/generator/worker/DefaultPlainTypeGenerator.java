package ru.gpb.utils.db.data.generator.worker;
// 2018.07.26 

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.function.Function;

/**
 * @author Boris Zhguchev
 */
public class DefaultPlainTypeGenerator extends AbstractPlainTypeGeneratorSupplier {

  private Random random = new Random(1000);
  private Long counter = 0l;

  private String counter(int max){
    String counter = this.counter.toString();
    if(counter.length() < max){
      return counter;
    }
    return counter.substring(0,max);
  }

  @Override
  public Function<MetaData.Column, String> string() {
    return c -> {
      int len = c.getLength();

      if (len == 0)
        return c.getColumn() + "-" + counter++;
      else if (len < 3)
        return counter(4);
      else {
        Random rand = new Random();
        char[] chars = new char[len-3];
        for (int i = 0; i < chars.length - 3; i++) {
          chars[i] = (char) (rand.nextInt(26) + 'a');
        }
        return String.valueOf(chars) + counter(4);
      }
    };
  }

  @Override
  public Function<MetaData.Column, BigDecimal> bigDecimal() {
    return unpack(BigDecimal.valueOf(random.nextInt()));
  }

  @Override
  public Function<MetaData.Column, Integer> integer() {
    return unpack(random.nextInt() * 10);
  }

  @Override
  public Function<MetaData.Column, Double> doubleVal() {
    return unpack(random.nextDouble());
  }

  @Override
  public Function<MetaData.Column, Date> date() {
    return unpack(new Date());
  }

  @Override
  public Function<MetaData.Column, Timestamp> timestamp() {
    return unpack(new Timestamp(new Date().getTime()));
  }


  @Override
  public Function<MetaData.Column, byte[]> bytes() {
    return p -> (p.toString() + "+" + random.nextLong()).getBytes();
  }

  private <R> Function<MetaData.Column, R> unpack(R r) {
    return p -> r;
  }
}
