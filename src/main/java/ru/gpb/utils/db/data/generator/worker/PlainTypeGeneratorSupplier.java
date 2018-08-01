package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import static ru.gpb.utils.db.data.generator.worker.MetaData.*;

/**
 * Basic interface supplier for generating values for plain fields.
 *
 * @author Boris Zhguchev
 */
public interface PlainTypeGeneratorSupplier {


  Function<MetaData.Column, UUID> uuid();

  Function<MetaData.Column, String> string();

  Function<MetaData.Column, BigDecimal> bigDecimal();

  Function<MetaData.Column, Integer> integer();

  Function<MetaData.Column, Double> doubleVal();

  Function<MetaData.Column, Date> date();
  Function<MetaData.Column, LocalDateTime> localDateTime();

  Function<MetaData.Column, Timestamp> timestamp();

  Function<MetaData.Column, Character> character();

  Function<MetaData.Column, byte[]> bytes();

  Function<MetaData.Column, Boolean> booleanV();

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
        return integer().apply(metaDataColumn);
      case "Double":
        return doubleVal().apply(metaDataColumn);
      case "Date":
        return date().apply(metaDataColumn);
      case "LocalDateTime":
        return localDateTime().apply(metaDataColumn);
      case "Timestamp":
        return timestamp().apply(metaDataColumn);
      case "Character":
      case "char":
        return character().apply(metaDataColumn);
      case "byte[]":
        return bytes().apply(metaDataColumn);
      case "boolean":
      case "Boolean":
        return booleanV().apply(metaDataColumn);
    }
    return null;
  }
}
