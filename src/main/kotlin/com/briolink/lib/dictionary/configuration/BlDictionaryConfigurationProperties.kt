package com.briolink.lib.dictionary.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "dictionary-service.api")
@Suppress("ConfigurationProperties")
data class BlDictionaryConfigurationProperties(
    val url: String,
    val version: String
)
