package ru.generator.db.data.converter.file;

import ru.generator.db.data.worker.MetaData;
import ru.generator.db.data.worker.MetaDataList;

import java.util.*;
import java.util.stream.Collectors;

import static ru.generator.db.data.converter.file.FileUtils.*;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
class FromFileStore {

  List<SmartRecord> richRawRecords;
  MetaDataList mdList;

  static FromFileStore init(MetaDataList mdList, List<String> records, StringTransformer transformer) {
	FromFileStore store = new FromFileStore();
	store.richRawRecords = matchRawWithMd(mdList, parseRaw(records));
	store.mdList = mdList;
	for (SmartRecord rec : store.richRawRecords) {
	  rec.buildPlain(transformer);
	}

	for (SmartRecord recordMd : store.richRawRecords) {
	  for (SmartRecord.Pair pair : recordMd.pairs) {
		String[] rawRecords = pair.records;
		MetaData md = recordMd.md;
		for (MetaData.Dependency dep : md.getDependencies().values()) {
		  String column = dep.getColumn();
		  recordMd.record
			.findValueBy(column, rawRecords)
			.filter(idStr -> idStr.length() > 0)
			.ifPresent(idStr -> {
			  MetaData depMd = dep.getMd();
			  Object id = transformer.transform(depMd.getId().getIdField().getType(), idStr);
			  findRawRecordMdByMd(depMd, store.richRawRecords)
				.ifPresent(smartRecord -> findObjectById(smartRecord.getObjects(), id, depMd)
				  .ifPresent(o -> md.setDependencyValue(pair.object, column, depMd, o)));
			});
		}

	  }
	}


	return store;
  }

  @SuppressWarnings("unchecked")
  <V> List<V> find(Class<V> vClass) {
	MetaData md = mdList.byClass(vClass).orElseThrow(ConverterMetadataException::new);
	for (SmartRecord rawRecord : richRawRecords) {
	  if (Objects.equals(rawRecord.md, md))
		return
		  rawRecord.pairs.stream()
			.map(e -> (V) e.object)
			.collect(Collectors.toList());
	}

	return new ArrayList<>();
  }


}
