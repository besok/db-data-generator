package ru.gpb.utils.db.data.generator.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"ru.gpb.utils.db.data.generator.worker.data"})
@EntityScan("ru.gpb.utils.db.data.generator.worker.data")
@Configuration
@SpringBootApplication
public class SpringBootConfig {
  public static void main(String[] args) {
    SpringApplication.run(SpringBootConfig.class);
  }



}
