package ru.gpb.utils.db.data.generator.worker.data;
// 2018.07.27 

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
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
  private UUID id;

  @Column(length = 10)
  private String name;
  private BigDecimal sum;

  private boolean plain;
  private Integer code;
  private Date created;

  @Column(name = "add_sum", precision = 19, scale = 4)
  private BigDecimal addSum;
}
