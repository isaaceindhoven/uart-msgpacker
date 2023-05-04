package nl.isaac.androidlibs.uart_msgpacker.model

import com.google.gson.annotations.SerializedName
import nl.isaac.androidlibs.uart_msgpacker.packer.*
import org.msgpack.core.MessagePack
import java.io.Serializable

open class ReadRequest(var rid: Int, var read: Array<Any>) : Packable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReadRequest

        if (rid != other.rid) return false
        if (!read.contentEquals(other.read)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rid
        result = 31 * result + read.contentHashCode()
        return result
    }

    /**
     * Packing method for the ReadRequest
     */
    override fun packRequest(shouldUseNewKeys: Boolean): ByteArray {
        return MessagePack.newDefaultBufferPacker().apply {
            packMapHeader(2)
            packString(UARTConstants.RID.serializationKey(shouldUseNewKeys))
            packInt(rid)
            read.let {
                packString(UARTConstants.READ.serializationKey(shouldUseNewKeys))
                packArrayHeader(it.size)
                packArray(it)
            }
            close()
        }.toByteArray()
    }
}

open class WriteRequest(var rid: Int, var write: Map<Any, Any?>) : Serializable, Packable {
    override fun packRequest(shouldUseNewKeys: Boolean): ByteArray {
        return MessagePack.newDefaultBufferPacker().apply {
            packMapHeader(2)
            packString(UARTConstants.RID.serializationKey(shouldUseNewKeys))
            packInt(rid)
            write.let {
                packString(UARTConstants.WRITE.serializationKey(shouldUseNewKeys))
                packMapHeader(write.size)
                packUARTMap(write)
            }
            close()
        }.toByteArray()
    }
}

data class ResetAllMessagesRequest(
    var rid: Int,
    @SerializedName("write")
    var resetModbusRegister: Map<Int, Int> = mapOf(40053 to 1)
) : Serializable, Packable {
    override fun packRequest(shouldUseNewKeys: Boolean): ByteArray {
        val register: Int = resetModbusRegister.entries.first().key
        val value: Int = resetModbusRegister.entries.first().value
        return MessagePack.newDefaultBufferPacker().apply {
            packMapHeader(2)
            packString(UARTConstants.RID.serializationKey(shouldUseNewKeys))
            packInt(rid)
            packString(UARTConstants.WRITE.serializationKey(shouldUseNewKeys))
            packMapHeader(1)
            packInt(register)
            packInt(value)

            close()
        }.toByteArray()
    }
}

data class ResetMessagesByIdRequest(
    var rid: Int,
    @SerializedName("resetMessages")
    var messageIDs: Array<Any>
) : Serializable, Packable {
    override fun packRequest(shouldUseNewKeys: Boolean): ByteArray {
        return MessagePack.newDefaultBufferPacker().apply {
            packMapHeader(2)
            packString(UARTConstants.RID.serializationKey(shouldUseNewKeys))
            packInt(rid)
            packString(UARTConstants.RESET_MESSAGES.serializationKey(shouldUseNewKeys))
            packArrayHeader(messageIDs.size)

            packArray(messageIDs)

            close()
        }.toByteArray()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResetMessagesByIdRequest

        if (rid != other.rid) return false
        if (!messageIDs.contentEquals(other.messageIDs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rid
        result = 12 * result + messageIDs.contentHashCode()
        return result
    }

}

data class ReadMessages(
    var timestamp: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0),
    var count: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReadMessages

        if (!timestamp.contentEquals(other.timestamp)) return false
        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        return 29 * count + timestamp.contentHashCode()
    }
}

data class ReadLog(
    var timestamp: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0),
    var count: Int = 500
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReadLog

        if (!timestamp.contentEquals(other.timestamp)) return false
        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        return 27 * count + timestamp.contentHashCode()
    }
}

data class GetMessagesRequest(
    var rid: Int,
    var readMessages: ReadMessages = ReadMessages()
) : Serializable, Packable {
    override fun packRequest(shouldUseNewKeys: Boolean): ByteArray {
        if (readMessages.timestamp.size != 6) throw IllegalArgumentException("Timestamp should be an int array of 6 numbers")
        return MessagePack.newDefaultBufferPacker().apply {
            packMapHeader(2)
            packString(UARTConstants.RID.serializationKey(shouldUseNewKeys))
            packInt(rid)
            packString(UARTConstants.READ_MESSAGES.serializationKey(shouldUseNewKeys))
            packMapHeader(2)
            packString(UARTConstants.COUNT.serializationKey(shouldUseNewKeys))
            packInt(readMessages.count)
            packString(UARTConstants.TIMESTAMP.serializationKey(shouldUseNewKeys))
            packArrayHeader(readMessages.timestamp.size)

            packIntArray(readMessages.timestamp)

            close()
        }.toByteArray()
    }
}

data class GetLogsRequest(
    var rid: Int,
    var readLog: ReadLog = ReadLog()
) : Serializable, Packable {
    override fun packRequest(shouldUseNewKeys: Boolean): ByteArray {
        if (readLog.timestamp.size != 6) throw IllegalArgumentException("Timestamp should be an int array of 6 numbers")
        return MessagePack.newDefaultBufferPacker().apply {
            packMapHeader(2)
            packString(UARTConstants.RID.serializationKey(shouldUseNewKeys))
            packInt(rid)
            packString(UARTConstants.READ_LOG.serializationKey(shouldUseNewKeys))
            packMapHeader(2)
            packString(UARTConstants.TIMESTAMP.serializationKey(shouldUseNewKeys))
            packArrayHeader(readLog.timestamp.size)
            packIntArray(readLog.timestamp)
            packString(UARTConstants.COUNT.serializationKey(shouldUseNewKeys))
            packInt(readLog.count)

            close()
        }.toByteArray()
    }
}
