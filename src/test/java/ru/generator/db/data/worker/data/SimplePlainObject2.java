package ru.generator.db.data.worker.data;
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
@Entity
@Data
public class SimplePlainObject2 {
  @Id
  @GeneratedValue
  private int id;

  private String name;

  @Column(precision = 10,scale = 5)
  private BigDecimal field1;
  @Column(precision = 10)
  private BigDecimal field2;
  @Column(precision = 10,scale = 9)
  private double field3;

}
