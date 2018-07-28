package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

/**
 *
 * Based abstract implementation for {@link PlainTypeGeneratorSupplier}.
 *
 * @author Boris Zhguchev
 */
public abstract class AbstractPlainTypeGeneratorSupplier implements PlainTypeGeneratorSupplier {
  @Override
  public Function<MetaData, UUID> uuid() {
    return p -> UUID.randomUUID();
  }

  @Override
  public Function<MetaData, String> string() {
    return p -> "";
  }

  @Override
  public Function<MetaData, BigDecimal> bigDecimal() {
    return p -> BigDecimal.ONE;
  }

  @Override
  public Function<MetaData, Integer> integer() {
    return p -> 1;
  }

  @Override
  public Function<MetaData, Double> doubleVal() {
    return p -> 1d;
  }

  @Override
  public Function<MetaData, Date> date() {
    return p -> new Date(0);
  }

  @Override
  public Function<MetaData, Timestamp> timestamp() {
    return p -> new Timestamp(0);
  }

  @Override
  public Function<MetaData, Character> character() {
    return p -> 'D';
  }

  @Override
  public Function<MetaData, byte[]> bytes() {
    return p -> "".getBytes();
  }

  @Override
  public Function<MetaData, Boolean> booleanV() {
    return p -> true;
  }


}
