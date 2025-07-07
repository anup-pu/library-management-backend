package com.library.library_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// Optional: Dotenv is only used locally
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@ComponentScan("com.library.library_management")
public class LibraryManagementApplication {

	public static void main(String[] args) {

		// Load .env only if variables are not already set (Render sets them automatically)
		if (System.getenv("DATABASE_URL") == null) {
			Dotenv dotenv = Dotenv.configure()
				.directory("library-management")  // root directory where your `.env` is during local run
				.filename(".env")
				.ignoreIfMissing() // prevents crash if .env not found
				.load();

			System.setProperty("DATABASE_URL", dotenv.get("DATABASE_URL"));
			System.setProperty("DATABASE_USERNAME", dotenv.get("DATABASE_USERNAME"));
			System.setProperty("DATABASE_PASSWORD", dotenv.get("DATABASE_PASSWORD"));
		}

		SpringApplication.run(LibraryManagementApplication.class, args);
	}
}
