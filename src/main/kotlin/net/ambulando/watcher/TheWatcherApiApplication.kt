package net.ambulando.watcher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories
class TheWatcherApiApplication

fun main(args: Array<String>) {
	runApplication<TheWatcherApiApplication>(*args)
}
