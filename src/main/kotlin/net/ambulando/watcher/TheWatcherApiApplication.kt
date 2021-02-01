package net.ambulando.watcher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TheWatcherApiApplication

fun main(args: Array<String>) {
	runApplication<TheWatcherApiApplication>(*args)
}
