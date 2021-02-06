package net.ambulando.watcher.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class OHLCs(
    @Id
    val id: String,

    @Indexed
    val interval: Int? = null,
    
    @Indexed
    val ohlcs: List<OHLC>
) 
