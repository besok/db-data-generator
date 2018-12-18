package ru.generator.db.data.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableJpaRepositories(basePackages = {"ru.generator.db.data.worker.data"})
@EntityScan("ru.generator.db.data.worker.data")
@Configuration
@SpringBootApplication
@EnableTransactionManagement
public class SpringBootConfig {


  public static void main(String[] args) {
    SpringApplication.run(SpringBootConfig.class);
  }

}
