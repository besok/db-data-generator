package ru.gpb.utils.db.data.generator.worker.data;
// 2018.07.27 

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Boris Zhguchev
 */
@Table(schema = "test", name = "simple_plain_object")
@Entity
@Data
public class SimplePlaiObjectGenId {
  @Id
  private int id;

  @Column(length = 10)
  private String name;
  private BigDecimal sum;

  private boolean plain;
  private Integer code;
  private Date created;

  private LocalDateTime ldt;
  private LocalDate ld;

  @Column(name = "add_sum", precision = 19, scale = 4)
  private BigDecimal addSum;
}
