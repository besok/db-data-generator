package ru.generator.db.data.converter.file;


import ru.generator.db.data.worker.MetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
class SmartRecord {
  RawRecord record;
  MetaData md;
  List<Pair> pairs;

  public SmartRecord(RawRecord record) {
	this.record = record;
	pairs = new ArrayList<>();
  }

  public List<Object> getObjects() {
	return pairs.stream().map(p -> p.object).collect(Collectors.toList());
  }

  public void setMd(MetaData md) {
	this.md = md;
  }

  @SuppressWarnings("unchecked")
  boolean buildPlain(StringTransformer transformer) {
	for (String[] records : record.values) {
	  try {
		Object instance = md.getAClass().newInstance();
		for (MetaData.Column col : md.getPlainColumns()) {
		  Optional<String> valOpt = record.findValueBy(col.getColumn(), records);
		  if (valOpt.isPresent()) {
			// FIXME: 13.01.2019 move to Metadata.Column
			col.getVal().set(instance, transformer.transform(col.getAClass(), valOpt.get()));
		  }
		}

		md.getId().getIdField().getType();
		record
		  .findValueBy(md.getId().getColumn(), records)
		  .ifPresent(el -> md.setIdValue(instance, transformer.transform(md.getId().getIdField().getType(), el)));
		pairs.add(new Pair(instance, records));

	  } catch (InstantiationException | IllegalAccessException e) {
		e.printStackTrace();
	  }
	}
	return true;
  }

  class Pair {
	Object object;
	String[] records;

	public Pair(Object object, String[] records) {
	  this.object = object;
	  this.records = records;
	}
  }
}
