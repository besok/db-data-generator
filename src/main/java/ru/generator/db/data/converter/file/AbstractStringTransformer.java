package ru.generator.db.data.converter.file;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
public abstract class AbstractStringTransformer implements StringTransformer {
  @Override
  public StringFunction<UUID> uuid() {
	return UUID::fromString;
  }

  @Override
  public StringFunction<String> string() {
	return s -> s;
  }

  @Override
  public StringFunction<BigDecimal> bigDecimal() {
	return BigDecimal::new;
  }

  @Override
  public StringFunction<Integer> integerVal() {
	return Integer::new;
  }

  @Override
  public StringFunction<Double> doubleVal() {
	return Double::new;
  }

  @Override
  public StringFunction<Date> date() {
	return null;
  }

  @Override
  public StringFunction<LocalDateTime> localDateTime() {
	return null;
  }

  @Override
  public StringFunction<LocalDate> localDate() {
	return null;
  }

  @Override
  public StringFunction<Timestamp> timestamp() {
	return null;
  }

  @Override
  public StringFunction<Character> character() {
	return s -> s.toCharArray()[0];
  }

  @Override
  public StringFunction<byte[]> bytes() {
	return String::getBytes;
  }

  @Override
  public StringFunction<Boolean> booleanVal() {
	return Boolean::new;
  }

  @Override
  public StringFunction<Long> longVal() {
	return Long::new;
  }

  @Override
  public StringFunction<Short> shortVal() {
	return Short::new;
  }

  @Override
  public StringFunction<? extends Enum<?>> enumVal() {
	return null;
  }

}
