package com.traanite.reline

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
class RelineApplicationTests {


	companion object {

		@Container
		@ServiceConnection
		@JvmStatic
		val mongoDb = MongoDBContainer("mongo:8.0.4").withReuse(true);

	}

	@Test
	fun contextLoads() {
	}

}
