package esi.roadside.assistance.provider.main.domain

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import esi.roadside.assistance.provider.main.domain.models.TypedNotification
import esi.roadside.assistance.provider.main.domain.models.NotificationType

class TypedNotificationDeserializer : JsonDeserializer<TypedNotification<*>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): TypedNotification<*> {
        val mapper = p.codec as ObjectMapper
        val root: JsonNode = mapper.readTree(p)

        val typeStr = root.get("type").asText()
        val dataType = NotificationType.valueOf(typeStr.uppercase())

        val dataNode = root.get("data")
        val dataObj = mapper.treeToValue(dataNode, dataType.clazz)

        return TypedNotification(dataType, dataObj)
    }
}