package ru.gpb.utils.db.data.generator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "ru.gpb.utils.db.data.generator")
public class DatabaseDataGeneratorConfig { }

// TODO: 7/25/2018 Корректно обработать связи одной таблицы друг с другом -> parent child ex - Limit
// TODO: 7/26/2018 Если в соседней таблице нет записей(m2m), то связи, естественно не будет. Нужно предсумотреть.
// TODO: 7/26/2018 Обработать ситуацию когда у связанной сущности нет репозитория.(создать бин?)
// TODO: 7/27/2018 тесты для коллекций, для разных баз ...
// TODO: 7/27/2018 добавить описание в readme
// TODO: 7/28/2018 Cache сделан листом, и поэтому может раздуться очень сильно. Нужно перенести с учетом кешсайза.
// TODO: 7/31/2018 Сделать дефолтный генератор(все методы описать а не только стринг)
// TODO: 8/1/2018 Возможность параллелить вычисления. Реализация ParallelGenerator. Каждого в отдельном потоке.
// TODO: 8/1/2018 Можно добавить каждого в отдельном потоке, но стартуют одинаково. 
// TODO: 8/1/2018 Добавить правила. правило пропускается через логику генератора ...