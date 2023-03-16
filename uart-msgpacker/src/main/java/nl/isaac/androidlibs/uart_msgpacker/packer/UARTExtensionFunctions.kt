package nl.isaac.androidlibs.uart_msgpacker.packer

import org.msgpack.core.MessagePacker
import org.msgpack.value.Value
import java.math.BigInteger

/**
 * packArray takes the readMap (an array of modbus registers to be read) and packs them as Ints.
 * An overloaded method exists in case you don't have type certainty. Please keep in mind that this might cause the value to be packed in a larger data type than strictly necessary.
 *
 * @param readArray the list of modbus registers that need to be read by the control unit.
 */
fun MessagePacker.packIntArray(readArray: Array<Int>) {
    for (entry in readArray) {
        packInt(entry)
    }
}

/**
 * The overloaded packArray method that allows the readMap (an array of modbus registers to be read) to have objects of any type and packs them according to a type check.
 * Please keep in mind that this might cause the value to be packed in a larger data type than strictly necessary.
 *
 * @param readArray the list of modbus registers that need to be read by the control unit, can be all types mixed.
 */
fun MessagePacker.packArray(readArray: Array<Any>) {
    for (entry in readArray) {
        when (entry) {
            is Byte -> packByte(entry)
            is Short -> packShort(entry)
            is Int -> packInt(entry)
            is Long -> packLong(entry)
            is Float -> packFloat(entry)
            is Double -> packDouble(entry)
            is BigInteger -> packBigInteger(entry)
            is Boolean -> packBoolean(entry)
            is String -> packString(entry)
            is Value -> packValue(entry)
            else -> throw Exception("Unknown type in packing: ${entry::class.java.simpleName}")
        }
    }
}

fun MessagePacker.packUARTMap(writeMap: Map<Any, Any?>) {
    for (entry in writeMap) {
        val key = entry.key
        val value = entry.value
        when (key) {
            is Byte -> packByte(key)
            is BigInteger -> packBigInteger(key)
            is Boolean -> packBoolean(key)
            is Double -> packDouble(key)
            is Float -> packFloat(key)
            is Int -> packInt(key)
            is Long -> packLong(key)
            is Short -> packShort(key)
            is String -> packString(key)
            is Value -> packValue(key)
            else -> throw Exception("Unknown type in packing: ${entry.key::class.java.simpleName}")
        }
        when (value) {
            null -> packNil()
            is Byte -> packByte(value)
            is BigInteger -> packBigInteger(value)
            is Boolean -> packBoolean(value)
            is Double -> packDouble(value)
            is Float -> packFloat(value)
            is Int -> packInt(value)
            is UInt -> packLong(value.toLong())
            is Long -> packLong(value)
            is Short -> packShort(value)
            is String -> packString(value)
            is Value -> packValue(value)
            is Map<*, *> -> {
                val localMap = value as Map<Any, Any?>
                packMapHeader(localMap.size)
                packUARTMap(localMap)
            }
            else -> throw Exception("Unknown type in packing: ${entry.value!!::class.java.simpleName}")
        }
    }
}

// Kotlin extension
internal fun ByteArray.toHexString(): String {
    val stringBuilder = StringBuilder(this.size)
    for (byteChar in this) {
        stringBuilder.append(String.format("%02X", byteChar))
    }
    return stringBuilder.toString()
}

internal fun String.stringToBytes(): ByteArray {
    val size = this.length
    if ((size % 2) != 0) {
        throw IllegalArgumentException()
    } else {
        return this.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}
