package ru.gpb.utils.db.data.generator.worker.data;
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
public class ManyToManyObjectLeft {

  @Id
  @GeneratedValue
  private int id;


  @ManyToMany
  @JoinTable
  private Set<ManyToManyObjectRight> right=new HashSet<>();
}
