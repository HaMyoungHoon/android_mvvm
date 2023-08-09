package mhha.sample.android_mvvm.models.adapter

import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class EmptyStringToNumberTypeAdapter : TypeAdapter<Number?>() {
    override fun write(jsonWriter: JsonWriter, number: Number?) {
        if (number == null) {
            jsonWriter.nullValue()
            return
        }
        jsonWriter.value(number)
    }


    override fun read(jsonReader: JsonReader): Number? {
        if (jsonReader.peek() === JsonToken.NULL) {
            jsonReader.nextNull()
            return null
        }
        return try {
            val value: String = jsonReader.nextString()
            if ("" == value) {
                0
            } else {
                if(value.contains(".")){
                    value.toDouble()
                } else {
                    value.toInt()
                }
            }

        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }
}
