package ru.generator.db.data;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;


@Configuration
@ComponentScan(basePackages = "ru.generator.db.data")
public class DatabaseDataGeneratorConfig {


}
// TODO: 7/26/2018 Если в соседней таблице нет записей(m2m), то связи, естественно не будет. Нужно предсумотреть.
// TODO: 7/27/2018 тесты для коллекций, для разных баз ...
// TODO: 10/9/2018 Перегруппировать тесты
////////// Need thinking ///////
// TODO: 8/1/2018 Сделать duplicateGenerator - (причем поколоночно и по записям)
// TODO: 8/18/2018 Дать возможность настраивать логику генерации связей между соседями.
// TODO: 20.12.2018 Операции с цикличными графами
////////// NIGHTMARE /////////

// TODO: 8/3/2018 Добавить возможность удалять данные из таблицы, нагенеренные и вообще все
// TODO: 8/2/2018 Сделать генерацию на основании таблиц с драйвера(без спринг дата):)