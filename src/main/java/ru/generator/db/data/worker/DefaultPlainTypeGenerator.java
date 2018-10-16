package ru.generator.db.data.worker;
// 2018.07.26 

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import java.util.function.Function;

/**
 * @author Boris Zhguchev
 */
public class DefaultPlainTypeGenerator extends AbstractPlainTypeGenerator {

  private Random random = new Random();
  private Long counter = 0L;

  private String counter(int max) {
    String counter = this.counter.toString();
    if (counter.length() < max) {
      return counter;
    }
    return counter.substring(0, max);
  }

  @Override
  public Function<MetaData.Column, String> string()  {
    return c -> {
      int len = c.getLength();

      if (len == 0)
        return c.getColumn() + "-" + counter++;
      else {
        // default 127 in db...
        if (len == 255) len = 127;
        Random rand = new Random();
        char[] chars = new char[len];
        for (int i = 0; i < chars.length; i++) {
          chars[i] = (char) (rand.nextInt(26) + 'a');
        }
        return String.valueOf(chars);
      }

    };
  }

  @Override
  public Function<MetaData.Column, BigDecimal> bigDecimal() {
    return unpack(BigDecimal.valueOf(random.nextInt()));
  }

  @Override
  public Function<MetaData.Column, Integer> integerVal() {
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

  @Override
  public Function<MetaData.Column, Long> longVal() {
    return unpack(random.nextLong());
  }

}
