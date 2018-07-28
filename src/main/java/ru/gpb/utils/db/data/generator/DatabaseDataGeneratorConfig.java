package ru.gpb.utils.db.data.generator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "ru.gpb.utils.db.data.generator")
public class DatabaseDataGeneratorConfig { }

// TODO: 7/26/2018 Если у колонки есть ограничение по длине, то это нужно считывать и запоминать.
// TODO: 7/25/2018 Корректно обработать связи одной таблицы друг с другом -> parent child ex - Limit
// TODO: 7/26/2018 Если в соседней таблице нет записей, то связи, естественно не будет. Нужно предсумотреть.
// TODO: 7/26/2018 Обработать ситуацию когда у связанной сущности нет репозитория.(создать бин?)
// TODO: 7/26/2018 возможность задавать алгоритм генератора для отдельного типа на лету.
// TODO: 7/27/2018 тесты для коллекций, для разных баз ...
// TODO: 7/27/2018 добавить описание в readme