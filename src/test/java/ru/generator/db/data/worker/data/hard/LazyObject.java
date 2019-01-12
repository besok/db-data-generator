package ru.generator.db.data.worker.data.hard;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Boris Zhguchev on 12/01/2019
 */
@Entity
@Table(schema = "test",name = "lazy_object")
@Data
public class LazyObject {

  @Id
  private Integer id;
  private String name;
  private Double aDouble;
  private Character character;



  @OneToMany(fetch = FetchType.LAZY)
  private List<MainObject> mainObjects = new ArrayList<>();



}
