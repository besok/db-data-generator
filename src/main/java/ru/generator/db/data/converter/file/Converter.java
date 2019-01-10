package ru.generator.db.data.converter.file;

import java.io.File;

/**
 * Created by Boris Zhguchev on 10/01/2019
 */
public interface Converter<V,S> {

  S to(V data);
  V from(S source);


}
