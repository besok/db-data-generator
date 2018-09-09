package ru.gpb.utils.db.data.generator.worker.data;
// 2018.08.13 

import lombok.Data;

import javax.persistence.*;

/**
 * @author Boris Zhguchev
 */
@Entity
@Data
@Table(schema = "test",name = "complex_object")
public class ComplexObject {

  @Id
  @GeneratedValue
  private int id;

  private String name;

  @ManyToOne(optional = false)
  @JoinColumn(name = "plain_id")
  private SimplePlainObject2 plain;
}
