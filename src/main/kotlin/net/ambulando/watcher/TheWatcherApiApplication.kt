package net.ambulando.watcher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories
@EnableConfigurationProperties
class TheWatcherApiApplication

fun main(args: Array<String>) {
	runApplication<TheWatcherApiApplication>(*args)
}
