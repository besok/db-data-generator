package ru.generator.db.data.worker.data.hard;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Boris Zhguchev on 12/01/2019
 */
@Entity
@Table(schema = "test", name = "hard_object")
@Data
public class MainObject {
  @Id
  private int idField;

  private String name;
  private BigDecimal bigDecimal;
  private UUID uuid;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "null_lazy_object_id")
  private LazyObject lazyObject;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lazy_object_id")
  private LazyObject lazyObject2;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "null_lazy_object_id_always_null")
  private LazyObject lazyObjectAlwaysNull;

  @OneToMany(fetch = FetchType.LAZY)
  private Set<NullLazyObject> lazyObjects = new HashSet<>();


}

