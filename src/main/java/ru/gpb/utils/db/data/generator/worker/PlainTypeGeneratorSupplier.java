package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

/**
 * Basic interface supplier for generating values for plain fields.
 *
 * @author Boris Zhguchev
 */
public interface PlainTypeGeneratorSupplier {


  Function<MetaData, UUID> uuid();

  Function<MetaData, String> string();

  Function<MetaData, BigDecimal> bigDecimal();

  Function<MetaData, Integer> integer();

  Function<MetaData, Double> doubleVal();

  Function<MetaData, Date> date();

  Function<MetaData, Timestamp> timestamp();

  Function<MetaData, Character> character();

  Function<MetaData, byte[]> bytes();

  Function<MetaData, Boolean> booleanV();

  default Object generate(Class<?> clazz, MetaData metaData) {
    switch (clazz.getSimpleName()) {
      case "String":
        return string().apply(metaData);
      case "UUID":
        return uuid().apply(metaData);
      case "BigDecimal":
        return bigDecimal().apply(metaData);
      case "Integer":
      case "int":
        return integer().apply(metaData);
      case "Double":
        return doubleVal().apply(metaData);
      case "Date":
        return date().apply(metaData);
      case "Timestamp":
        return timestamp().apply(metaData);
      case "Character":
      case "char":
        return character().apply(metaData);
      case "byte[]":
        return bytes().apply(metaData);
      case "boolean":
      case "Boolean":
        return booleanV().apply(metaData);
    }
    return null;
  }
}
