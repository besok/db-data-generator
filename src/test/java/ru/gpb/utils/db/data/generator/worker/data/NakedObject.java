package ru.gpb.utils.db.data.generator.worker.data;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Boris Zhguchev on 26/08/2018
 */
@Entity
@Data
public class NakedObject {
  @Id
  private int id;


  private String field;
  private String fieldWithCamel;
  private String FieldWithCamelCase;
}
