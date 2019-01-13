package ru.generator.db.data.converter.file;

import ru.generator.db.data.worker.MetaData;
import ru.generator.db.data.worker.MetaDataList;

import java.util.*;
import java.util.stream.Collectors;

import static ru.generator.db.data.converter.file.RawUtility.*;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
class RecordStore {

  List<RawRecordMd> richRawRecords;
  MetaDataList mdList;
  Map<MetaData,List<?>> objectsStore;


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
  static RecordStore init(MetaDataList mdList, List<String> records,StringTransformer transformer) {
    RecordStore store = new RecordStore();
	store.richRawRecords = matchRaw(mdList, splitRaw(records));
	store.mdList=mdList;
	store.objectsStore=new HashMap<>();
	for (RawRecordMd rec : store.richRawRecords) {
	  List<Object> obj = rec.buildPlain(transformer);
	  store.objectsStore.put(rec.md,obj);
	}

	for (RawRecordMd record : store.richRawRecords) {

	}

	return store;
  }

  @SuppressWarnings("unchecked")
  <V> List<V> find(Class<V> vClass){
	MetaData md = mdList.byClass(vClass).orElseThrow(ConverterMetadataException::new);
	List<?> objects = objectsStore.get(md);
	if(Objects.isNull(objects))
	  throw  new ConverterMetadataException();

	return (List<V>) objects;

  }



}
