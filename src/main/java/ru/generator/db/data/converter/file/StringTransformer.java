package ru.generator.db.data.converter.file;

import ru.generator.db.data.worker.MetaData;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
public interface StringTransformer {
  StringFunction<UUID> uuid();

  StringFunction<String> string();

  StringFunction<BigDecimal> bigDecimal();

  StringFunction<Integer> integerVal();

  StringFunction< Double> doubleVal();

  StringFunction<Date> date();

  StringFunction<LocalDateTime> localDateTime();

  StringFunction<LocalDate> localDate();

  StringFunction<Timestamp> timestamp();

  StringFunction<Character> character();

  StringFunction<byte[]> bytes();

  StringFunction<Boolean> booleanVal();

  StringFunction<Long> longVal();

  StringFunction< Short> shortVal();

  StringFunction< ? extends Enum<?>> enumVal();

  default Object transform(Class<?> clazz, String val) {
	switch (clazz.getSimpleName()) {
	  case "String":
		return string().apply(val);
	  case "UUID":
		return uuid().apply(val);
	  case "BigDecimal":
		return bigDecimal().apply(val);
	  case "Integer":
	  case "int":
		return integerVal().apply(val);
	  case "Double":
	  case "double":
		return doubleVal().apply(val);
	  case "Date":
		return date().apply(val);
	  case "Short":
	  case "short":
		return shortVal().apply(val);
	  case "LocalDateTime":
		return localDateTime().apply(val);
	  case "LocalDate":
		return localDate().apply(val);
	  case "Timestamp":
		return timestamp().apply(val);
	  case "Character":
	  case "char":
		return character().apply(val);
	  case "byte[]":
		return bytes().apply(val);
	  case "boolean":
	  case "Boolean":
		return booleanVal().apply(val);
	  case "long":
	  case "Long":
		return longVal().apply(val);
	}

	if (!Objects.isNull(clazz.getSuperclass())
	  && clazz.getSuperclass().equals(Enum.class)) {
	  return enumVal().apply(val);
	}

	return null;
  }

  interface StringFunction<V> extends Function<String,V>{
	@Override
	V apply(String s);
  }
}
