package ru.gpb.utils.db.data.generator.worker.data;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Boris Zhguchev on 01/10/2018
 */
@Entity
@Data
public class SeqIncObject {
  @Id
  private int id;

  private int iLeft;
  private int iRight;

  private Long lLeft;
  private long lRight;

  private int random;
}
