package ru.generator.db.data.worker.data.constraints;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Boris Zhguchev on 22/12/2018
 */
@Data
@Entity
@Table(name = "only_right",schema = "test")
public class EntityRight {
  @Id
  @GeneratedValue
  private int id;




}
