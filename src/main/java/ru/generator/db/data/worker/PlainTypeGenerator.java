package ru.generator.db.data.worker;
// 2018.07.24 

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Basic interface supplier for generating values for plain fields.
 *
 * @author Boris Zhguchev
 */
public interface PlainTypeGenerator {


  Function<MetaData.Column, UUID> uuid();

  Function<MetaData.Column, String> string();

  Function<MetaData.Column, BigDecimal> bigDecimal();

  Function<MetaData.Column, Integer> integerVal();

  Function<MetaData.Column, Double> doubleVal();

  Function<MetaData.Column, Date> date();

  Function<MetaData.Column, LocalDateTime> localDateTime();

  Function<MetaData.Column, LocalDate> localDate();

  Function<MetaData.Column, Timestamp> timestamp();

  Function<MetaData.Column, Character> character();

  Function<MetaData.Column, byte[]> bytes();

  Function<MetaData.Column, Boolean> booleanVal();

  Function<MetaData.Column, Long> longVal();

  Function<MetaData.Column, Short> shortVal();

  Function<MetaData.Column, ? extends Enum<?>> enumVal();


  default Object generate(Class<?> clazz, MetaData.Column metaDataColumn) {
	switch (clazz.getSimpleName()) {
	  case "String":
		return string().apply(metaDataColumn);
	  case "UUID":
		return uuid().apply(metaDataColumn);
	  case "BigDecimal":
		return bigDecimal().apply(metaDataColumn);
	  case "Integer":
	  case "int":
		return integerVal().apply(metaDataColumn);
	  case "Double":
	  case "double":
		return doubleVal().apply(metaDataColumn);
	  case "Date":
		return date().apply(metaDataColumn);
	  case "short":
	  case "Short":
	    return shortVal().apply(metaDataColumn);
	  case "LocalDateTime":
		return localDateTime().apply(metaDataColumn);
	  case "LocalDate":
		return localDate().apply(metaDataColumn);
	  case "Timestamp":
		return timestamp().apply(metaDataColumn);
	  case "Character":
	  case "char":
		return character().apply(metaDataColumn);
	  case "byte[]":
		return bytes().apply(metaDataColumn);
	  case "boolean":
	  case "Boolean":
		return booleanVal().apply(metaDataColumn);
	  case "long":
	  case "Long":
		return longVal().apply(metaDataColumn);
	}

	if (!Objects.isNull(clazz.getSuperclass())
	  && clazz.getSuperclass().equals(Enum.class)) {
	  return enumVal().apply(metaDataColumn);
	}

	return null;
  }
}
