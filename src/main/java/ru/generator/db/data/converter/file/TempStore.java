package ru.generator.db.data.converter.file;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Boris Zhguchev on 12/01/2019
 */
public class TempStore {

  private Map<String, Value> store;


  public TempStore() {
	store = new HashMap<>();
  }

  public void init(String header) {
	store.putIfAbsent(header,new Value(header));
  }

  public void addColHeader(String k,String colHeader){
    store.get(k).colHeader(colHeader);
  }
  public void addRecord(String k,String record){
    store.get(k).add(record);
  }

  public List<String> prepare() {
	return store.values().stream().flatMap(Value::prepare).collect(Collectors.toList());
  }

  private static class Value {
	String colHeader;
	String header;
	Set<String> records;
	String del = "@@@";

	public Value(String header) {
	  this.header = header;
	  records = new HashSet<>();
	}

	void add(String record) {
	  records.add(record);
	}
    void colHeader(String colHeader){
	  this.colHeader=colHeader;
	}
	Stream<String> prepare() {
	  List<String> res = new ArrayList<>();
	  res.add(del+header);
	  res.add(colHeader);
	  res.addAll(records);
	  return res.stream();
	}
  }
}
