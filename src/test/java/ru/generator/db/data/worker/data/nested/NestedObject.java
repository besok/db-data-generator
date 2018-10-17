package ru.generator.db.data.worker.data.nested;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Boris Zhguchev on 17/10/2018
 */
@Entity
@Data
public class NestedObject {
  @Id
  private int id;

  @Column(nullable = false)
  private SimpleObject field;
}
