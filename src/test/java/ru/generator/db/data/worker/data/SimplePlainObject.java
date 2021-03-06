package ru.generator.db.data.worker.data;
// 2018.07.27 

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * @author Boris Zhguchev
 */
@Table(schema = "test", name = "simple_plain_object")
@Entity
@Data
public class SimplePlainObject {
  @Id
  @GeneratedValue
  private int id;

  @Column(length = 1)
  private String shortName;

  @Column(length = 10)
  private String name;
  private BigDecimal sum;

  private short shortV1;
  private Short shortV2;

  private boolean plain;
  private Integer code;
  private Date created;

  private LocalDateTime ldt;

  @Column(name = "add_sum", precision = 19, scale = 4)
  private BigDecimal addSum;
}
