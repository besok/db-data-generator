package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

/**
 *
 * Based abstract implementation for {@link PlainTypeGenerator}.
 *
 * @author Boris Zhguchev
 */
public abstract class AbstractPlainTypeGenerator implements PlainTypeGenerator {
  @Override
  public Function<MetaData.Column, UUID> uuid() {
    return unpack(UUID.randomUUID());
  }

  @Override
  public Function<MetaData.Column, String> string() {
    return unpack("");
  }

  @Override
  public Function<MetaData.Column, BigDecimal> bigDecimal() {
    return unpack(BigDecimal.ONE);
  }

  @Override
  public Function<MetaData.Column, Integer> integer() {
    return p -> 1;
  }

  @Override
  public Function<MetaData.Column, Double> doubleVal() {
    return unpack(1d);
  }

  @Override
  public Function<MetaData.Column, Date> date() {
    return unpack(new Date(0));
  }

  @Override
  public Function<MetaData.Column, Timestamp> timestamp() {
    return unpack(new Timestamp(0));
  }

  @Override
  public Function<MetaData.Column, Character> character() {
    return unpack('A');
  }

  @Override
  public Function<MetaData.Column, byte[]> bytes() {
    return unpack("".getBytes());
  }

  @Override
  public Function<MetaData.Column, Boolean> booleanV() {
    return unpack(true);
  }

  @Override
  public Function<MetaData.Column, LocalDateTime> localDateTime() {
    return unpack(LocalDateTime.now());
  }

  @Override
  public Function<MetaData.Column, Long> longV() {
    return unpack(0L);
  }

  protected  <R> Function<MetaData.Column, R> unpack(R r) {
    return p -> r;
  }


}
