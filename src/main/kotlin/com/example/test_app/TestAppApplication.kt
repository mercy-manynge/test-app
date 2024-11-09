package com.example.test_app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.example.test_app")
class TestAppApplication {
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			runApplication<TestAppApplication>(*args)
		}
	}
}