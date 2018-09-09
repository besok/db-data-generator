package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.logging.Logger;

import static java.util.stream.Collectors.*;

/**
 * Cache implementation.
 * It is needed for making relations between tables many2many.
 *
 * @author Boris Zhguchev
 */
@Service
@SuppressWarnings("unchecked")
public class InnerCache {
  private Logger LOGGER = Logger.getLogger(DatabaseDataGeneratorFactory.class.getName());

  /**
   * this is a cache size for generation many2many relation.
   */
  @Value("${generator.cache-entity-size:20}")
  private int cacheSize;

  protected Repositories repositories;
  protected final MetaDataList metaDataList;

  private ConcurrentMap<MetaData, List<Object>> cache;
  /**
   * Get cache snapshot
   *
   * @return map consists of pair key + value list's size.
   */
  // TODO: 9/9/2018 Сделать обьект Snapshot и сделать вызов
  public Map<MetaData, Integer> snapshot() {
    return cache
        .entrySet()
        .stream()
        .collect(toMap(Map.Entry::getKey, e -> e.getValue().size()));
  }

  public int cacheEntitySize() {
    return cacheSize;
  }

  /**
   * get value list by Class
   */
  public <T> List<T> getValueList(Class<T> cl) {
    return
        metaDataList
            .byClass(cl)
            .map(cache::get)
            .map(l -> castList(l, cl))
            .orElseGet(ArrayList::new);
  }


  /**
   * get value list by {@link MetaData}
   */
  List<Object> getValueList(MetaData p) {
    List<Object> obj = cache.get(p);
    return obj == null ? new ArrayList<>() : obj;
  }

  private <T> List<T> castList(List<Object> list, Class<T> cl) {
    return list.stream().map(cl::cast).collect(toList());
  }


  public InnerCache(MetaDataList metaDataList, ApplicationContext context) {
    this.metaDataList = metaDataList;
    repositories = new Repositories(context);
    cache = new ConcurrentHashMap<>();
  }


  void put(MetaData metaData, Object o) {
    cache.compute(metaData, (p, l) -> {
      if (l == null)
        l = new ArrayList<>();
      if (l.size() >= cacheSize) {
        int s = l.size();
        l = new ArrayList<>(l.subList(s / 4, s));
      }
      l.add(o);
      return l;
    });
  }

  int getValueSize(MetaData p) {
    List<Object> obj = cache.get(p);
    return obj == null ? 0 : obj.size();
  }

  Set<MetaData> metas() {
    return cache.keySet();
  }

  /**
   * filling cache from db.
   */
  void warm() {
    init();
    for (MetaData metaData : metas()) {
      warmingByMetaData(metaData);
    }
    LOGGER.info("cache's been initialized : " + mapToString(snapshot()));
  }

  private void init() {
    assertCacheSizeIsNull();
    for (MetaData metaData : this.metaDataList.getMetaDataList()) {
      cache.put(metaData, new ArrayList<>());
    }
  }

  private void assertCacheSizeIsNull() {
    if (cacheSize == 0) {
      throw new IllegalStateGeneratorException("generator.cache-entity-size must not be 0 or null. By default it is 20.");
    }
  }

  private void warmingByMetaData(MetaData metaData) {
    if (getValueSize(metaData) == 0) {
      repositories
          .getRepositoryFor(metaData.getAClass())
          .ifPresent(r -> findPage(metaData, (JpaRepository) r));
    }
  }

  private void findPage(MetaData metaData, JpaRepository r) {
    r.findAll(PageRequest.of(0, cacheSize)).forEach(o -> put(metaData, o));
  }

  private String mapToString(Map<? extends MetaData, ? extends Number> map) {
    return map.entrySet()
        .stream()
        .map(e -> e.getKey().getAClass().getName() + "=" + e.getValue())
        .collect(joining(","));
  }


}
