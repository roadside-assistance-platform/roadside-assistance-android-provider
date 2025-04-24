package esi.roadside.assistance.provider.main.di

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import esi.roadside.assistance.provider.main.domain.TypedNotificationDeserializer
import esi.roadside.assistance.provider.main.domain.models.TypedNotification
import org.koin.dsl.module

val mainModule = module {
    single {
        val mapper = ObjectMapper()
        val module = SimpleModule()
        module.addDeserializer(TypedNotification::class.java, TypedNotificationDeserializer())
        mapper.registerModule(module)
        return@single mapper
    }
    includes(viewModelModule, useCaseModule, repoModule)
}