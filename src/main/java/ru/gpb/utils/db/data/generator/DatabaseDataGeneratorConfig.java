package ru.gpb.utils.db.data.generator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
@ComponentScan(basePackages = "ru.gpb.utils.db.data.generator")
public class DatabaseDataGeneratorConfig {


}

// TODO: 7/25/2018 Корректно обработать связи одной таблицы друг с другом -> parent child ex - Limit
// TODO: 7/26/2018 Если в соседней таблице нет записей(m2m), то связи, естественно не будет. Нужно предсумотреть.
// TODO: 7/26/2018 Обработать ситуацию когда у связанной сущности нет репозитория.(создать бин?)
// TODO: 7/27/2018 тесты для коллекций, для разных баз ...
// TODO: 7/27/2018 добавить описание в readme
// TODO: 8/1/2018 Добавить правила. правило пропускается через логику генератора ...
// TODO: 8/6/2018 Если сначала metronome а потом  repeate то в report, и т.д. теряются все предыдущие операции. Нужен CTX

////////// Need thinking ///////
// TODO: 8/1/2018 Сделать duplicateGenerator - (причем поколоночно и по записям)
// TODO: 8/1/2018 Можно добавить каждого в отдельном потоке, но стартуют одинаково. Называется race gen


////////// NIGHTMARE /////////
// TODO: 8/3/2018 Добавить возможность удалять данные из таблицы, нагенеренные и вообще все(сложно)
// TODO: 8/2/2018 Сделать генерацию на основании таблиц с драйвера(без спринг дата):)