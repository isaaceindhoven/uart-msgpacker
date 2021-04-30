package nl.isaac.androidlibs.uart_msgpacker.gson

import com.google.gson.*
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * This Adapter is necessary so Gson does not deserialize everything to Doubles.
 * It also allows for Int typing on map keys, as this is common in the UART solution.
 */
internal class NumberTypeAdapter : TypeAdapter<Any>() {

    private val delegate = Gson().getAdapter(Any::class.java)

    override fun write(out: JsonWriter?, value: Any?) {
        delegate.write(out, value)
    }

    override fun read(`in`: JsonReader?): Any? {
        return when (`in`?.peek()) {
            JsonToken.BEGIN_ARRAY -> {
                val list: MutableList<Any?> = ArrayList()
                `in`.beginArray()
                while (`in`.hasNext()) {
                    list.add(read(`in`))
                }
                `in`.endArray()
                list.toTypedArray()
            }
            JsonToken.BEGIN_OBJECT -> {
                val map: MutableMap<Any, Any?> =
                    LinkedTreeMap()
                `in`.beginObject()
                while (`in`.hasNext()) {
                    var name = `in`.nextName()
                    var modbusName: Int? = null
                    try {
                        modbusName = name.toInt()
                    } catch (ex: Exception) {
                        // Do Nothing
                    }
                    if (modbusName == null) {
                        map[name] = read(`in`)
                    } else {
                        map[modbusName] = read(`in`)
                    }
                }
                `in`.endObject()
                map
            }
            JsonToken.STRING -> `in`.nextString()

            JsonToken.NUMBER -> {
                val n: String = `in`.nextString()

                if (n.indexOf('.') != -1) {
                    n.toDouble()
                } else {
                    try {
                        n.toInt()
                    } catch (ex: Exception) {
                        n.toLong()
                    }
                }
            }
            JsonToken.BOOLEAN -> `in`.nextBoolean()
            JsonToken.NULL -> {
                `in`.nextNull()
                null
            }
            else -> throw IllegalStateException()
        }
    }
}