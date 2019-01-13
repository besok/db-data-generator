package ru.generator.db.data.converter.file;

import ru.generator.db.data.worker.MetaData;
import ru.generator.db.data.worker.MetaDataList;

import java.util.*;
import java.util.stream.Collectors;

import static ru.generator.db.data.converter.file.RawUtility.*;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
class FromFileStore {

  List<RawRecordMd> richRawRecords;
  MetaDataList mdList;


  private static List<RawRecordMd> matchRaw(MetaDataList mdList, List<RawRecord> records) {
	return records.stream()
	  .map(RawRecordMd::new)
	  .map(r -> {
		String[] schTbl = r.record.header.split("\\.");
		mdList.bySchemaTable(schTbl[0], schTbl[1]).ifPresent(r::setMd);
		return r;
	  })
	  .collect(Collectors.toList());
  }

  private static List<RawRecord> splitRaw(List<String> records) {

	List<RawRecord> raws = new ArrayList<>();
	RawRecord r = new RawRecord();
	boolean f = false;
	for (String record : records) {
	  if (isHeader(record)) {
		raws.add(r);
		r = new RawRecord();
		r.header = getHeader(record);
		f = true;
	  } else {
		if (f) {
		  r.colHeaders = getColHeaders(record);
		  f = false;
		} else {
		  r.values.add(getRecords(record, r.colHeaders));
		}
	  }
	}
	raws.add(r);
	raws.removeIf(el -> Objects.isNull(el.header));
	return raws;
  }

  static FromFileStore init(MetaDataList mdList, List<String> records, StringTransformer transformer) {
	FromFileStore store = new FromFileStore();
	store.richRawRecords = matchRaw(mdList, splitRaw(records));
	store.mdList = mdList;
	for (RawRecordMd rec : store.richRawRecords) {
	  rec.buildPlain(transformer);
	}

	for (RawRecordMd recordMd : store.richRawRecords) {
	  for (RawRecordMd.Pair pair : recordMd.pairs) {
		Object object = pair.object;
		String[] rawRecords = pair.records;
		MetaData md = recordMd.md;
		for (MetaData.Dependency dep : md.getDependencies().values()) {
		  String column = dep.getColumn();
		  recordMd.record.findValueBy(column, rawRecords).ifPresent(idStr -> {
			if (idStr.length() > 0) {
			  MetaData depMd = dep.getMd();
			  Class<?> type = depMd.getId().getIdField().getType();
			  Object id = transformer.transform(type, idStr);
			  findByMd(depMd, store.richRawRecords)
				.ifPresent(rawRecordMd -> findById(rawRecordMd.getObjects(), id, depMd)
				  .ifPresent(o -> md.setDependencyValue(object,column, depMd, o)));
			}
		  });
		}

	  }
	}


	return store;
  }

  private static Optional<Object> findById(List<Object> objects, Object id, MetaData md) {
	for (Object object : objects) {
	  Object idValue = md.getIdValue(object);
	  if (Objects.equals(idValue, id))
		return Optional.of(object);
	}
	return Optional.empty();
  }

  private static Optional<RawRecordMd> findByMd(MetaData md, List<RawRecordMd> rawRecordMds) {
	for (RawRecordMd rawRecordMd : rawRecordMds) {
	  if (Objects.equals(rawRecordMd.md, md))
		return Optional.of(rawRecordMd);
	}
	return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  <V> List<V> find(Class<V> vClass) {
	MetaData md = mdList.byClass(vClass).orElseThrow(ConverterMetadataException::new);
	for (RawRecordMd rawRecord : richRawRecords) {
	  if (Objects.equals(rawRecord.md, md))
		return
		  rawRecord.pairs.stream()
			.map(e -> (V) e.object)
			.collect(Collectors.toList());
	}

	return new ArrayList<>();
  }


}
