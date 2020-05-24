package nextg.telegrambot;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

@SpringBootApplication
public class BotApp {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(BotApp.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

/*	@Bean
	public DataSource getDataSource() {
		return DataSourceBuilder.create()
				.driverClassName("org.h2.Driver")
				.url("jdbc:h2:mem:test")
				.username("SA")
				.password("")
				.build();
	}*/
	@Bean
	public DataSource getDataSource() {
		return DataSourceBuilder.create()
				.url("jdbc:postgresql://postgre:5432/telegrambot")
				.driverClassName("org.postgresql.Driver")
				.username("postgres")
				.password("postgres42")
				.build();
	}
}
