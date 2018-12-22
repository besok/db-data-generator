package ru.generator.db.data.worker.data.withoutrepo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Boris Zhguchev on 22/12/2018
 */
@Entity
@Data
@Table(schema = "test",name = "without_repo")
public class WithoutRepo {
  @Id
  @GeneratedValue
  private int id;

  private String val;
}
