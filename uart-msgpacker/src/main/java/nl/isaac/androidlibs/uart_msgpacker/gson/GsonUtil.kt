package nl.isaac.androidlibs.uart_msgpacker.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

internal class GsonUtil {
    companion object {
        fun getGson(): Gson {
            val mapType: Type = object : TypeToken<Map<Any, Any>>() {}.type
            val responseMapType: Type = object : TypeToken<Map<Int, Any?>>() {}.type
            val arrayType: Type = object : TypeToken<Array<Any>>() {}.type
            val listType: Type = object : TypeToken<List<Any>>() {}.type

            return GsonBuilder()
                .registerTypeAdapter(mapType, NumberTypeAdapter())
                .registerTypeAdapter(responseMapType, NumberTypeAdapter())
                .registerTypeAdapter(listType, NumberTypeAdapter())
                .registerTypeAdapter(arrayType, NumberTypeAdapter())
                .create()
        }
    }
}