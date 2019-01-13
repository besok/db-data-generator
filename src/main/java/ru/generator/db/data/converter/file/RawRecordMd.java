package ru.generator.db.data.converter.file;


import ru.generator.db.data.worker.MetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
class RawRecordMd {
  RawRecord record;
  MetaData md;
  List<Pair> pairs;

  public List<Object> getObjects(){
    return pairs.stream().map(p -> p.object).collect(Collectors.toList());
  }

  public RawRecordMd(RawRecord record) {
	this.record = record;
	pairs=new ArrayList<>();
  }

  public void setMd(MetaData md) {
	this.md = md;
  }
  @SuppressWarnings("unchecked")
  boolean buildPlain(StringTransformer transformer) {
	for (String[] rawRecords : record.values) {
	  try {
		Object instance = md.getAClass().newInstance();
		for (MetaData.Column col : md.getPlainColumns()) {
		  Optional<String> valOpt = record.findValueBy(col.getColumn(), rawRecords);
		  if (valOpt.isPresent()) {
			String v = valOpt.get();
			// FIXME: 13.01.2019 move to Metadata.Column
			col.getVal().set(instance, transformer.transform(col.getAClass(), v));
		  }
		}

		record
		  .findValueBy(md.getId().getColumn(), rawRecords)
		  .ifPresent(el -> md.setIdValue(instance, transformer.transform(md.getId().getIdField().getType(), el)));

		pairs.add(new Pair(instance,rawRecords));
	  } catch (InstantiationException | IllegalAccessException e) {
		e.printStackTrace();
	  }
	}
	return true;
  }

  class Pair{
    Object object;
    String[] records;

	public Pair(Object object, String[] records) {
	  this.object = object;
	  this.records = records;
	}
  }
}
