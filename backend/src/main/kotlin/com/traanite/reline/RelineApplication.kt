package com.traanite.reline

import com.traanite.reline.gasprices.Scraper
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RelineApplication : CommandLineRunner {
	override fun run(vararg args: String?) {
		Scraper().run()
	}
}

fun main(args: Array<String>) {
	runApplication<RelineApplication>(*args)
}
