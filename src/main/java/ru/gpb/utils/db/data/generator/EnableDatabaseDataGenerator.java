package ru.gpb.utils.db.data.generator;


import org.springframework.context.annotation.Import;
import ru.gpb.utils.db.data.generator.worker.DatabaseDataGeneratorFactory;

import java.lang.annotation.*;

/**
 *
 * Annotation to enable database data plainValueGenerator @see {@link DatabaseDataGeneratorFactory}
 * This annotation allows to scan all packages for this app and add all founded beans to parent spring app.
 *
 *
 * @author Boris Zhguchev
 * */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented @Inherited
@Import(DatabaseDataGeneratorConfig.class)
public @interface EnableDatabaseDataGenerator { }
