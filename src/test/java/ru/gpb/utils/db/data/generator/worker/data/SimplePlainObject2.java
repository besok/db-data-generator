package ru.gpb.utils.db.data.generator.worker.data;
// 2018.07.27 

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Boris Zhguchev
 */
@Table(schema = "test", name = "simple_plain_object_2")
@Entity @Data
public class SimplePlainObject2 {
  @Id
  @GeneratedValue
  private int id;

  private String name;


}
