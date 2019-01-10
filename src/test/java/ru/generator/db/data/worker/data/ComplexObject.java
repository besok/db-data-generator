package ru.generator.db.data.worker.data;
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
  private String name1;
  private String name2;
  private String name3;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "plain_id")
  private SimplePlainObject2 plain;
}
