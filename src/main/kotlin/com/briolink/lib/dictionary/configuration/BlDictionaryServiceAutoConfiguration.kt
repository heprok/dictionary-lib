package com.briolink.lib.dictionary.configuration

import com.briolink.lib.dictionary.service.DictionaryService
import com.briolink.lib.dictionary.service.WebClientDictionaryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@ComponentScan("com.briolink.lib.dictionary.service")
@EnableConfigurationProperties(
    BlDictionaryConfigurationProperties::class
)
@ConditionalOnProperty(prefix = "dictionary-service.api", name = ["url", "version"])
class BlDictionaryServiceAutoConfiguration {
    @Value("\${dictionary-service.api.url}")
    lateinit var urlApi: String

    @Value("\${dictionary-service.api.version}")
    lateinit var apiVersion: String

    @Bean
    @Primary
    fun webClientDictionaryService() = WebClientDictionaryService(WebClient.create("$urlApi/api/v$apiVersion/"))

    @Bean
    fun dictionaryService(webClientPS: WebClientDictionaryService) = DictionaryService(webClientPS)
}
