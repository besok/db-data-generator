package ru.generator.db.data.converter.file;


import ru.generator.db.data.worker.MetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
class RawRecordMd {
  RawRecord record;
  MetaData md;

  public RawRecordMd(RawRecord record) {
	this.record = record;
  }

  public void setMd(MetaData md) {
	this.md = md;
  }
  @SuppressWarnings("unchecked")
  <V>List<V> buildPlain(StringTransformer transformer) {
	List<V> objects = new ArrayList<>();
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

		objects.add((V) instance);
	  } catch (InstantiationException | IllegalAccessException e) {
		e.printStackTrace();
	  }
	}
	return objects;
  }

}
