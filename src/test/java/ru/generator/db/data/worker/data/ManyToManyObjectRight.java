package ru.generator.db.data.worker.data;
// 2018.08.13 

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Boris Zhguchev
 */
@Entity
@Data
@Table(schema = "test")
public class ManyToManyObjectRight {

  @Id
  @GeneratedValue
  private int id;


  @ManyToMany
  private Set<ManyToManyObjectLeft> left=new HashSet<>();
}
