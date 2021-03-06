package nl.isaac.androidlibs.uart_msgpacker.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ReadResponse(var rid: Int, var read: Map<Int, Any?>) : Serializable

data class WriteResponse(var rid: Int, var result: Int?, var write: Map<Int, Any?>) : Serializable

data class ResetMessagesResponse(var rid: Int, var resetMessages: Map<Int, Any?>) : Serializable

data class Message(var mid: Int,
                   @SerializedName("endoflist")
                   var endOfList: Array<Int>?,
                   @SerializedName("occured")
                   var occured: Array<Int>?,
                   @SerializedName("reset")
                   var reset: Array<Int>?,
                   @SerializedName("rootcausedisappeared")
                   var rootcause: Array<Int>?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (mid != other.mid) return false
        if (endOfList != null) {
            if (other.endOfList == null) return false
            if (!endOfList.contentEquals(other.endOfList)) return false
        } else if (other.endOfList != null) return false
        if (occured != null) {
            if (other.occured == null) return false
            if (!occured.contentEquals(other.occured)) return false
        } else if (other.occured != null) return false
        if (!reset.contentEquals(other.reset)) return false
        if (!rootcause.contentEquals(other.rootcause)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mid
        result = 31 * result + (endOfList?.contentHashCode() ?: 0)
        result = 31 * result + (occured?.contentHashCode() ?: 0)
        result = 31 * result + reset.contentHashCode()
        result = 31 * result + rootcause.contentHashCode()
        return result
    }
}

data class MessageResponse(var rid: Int, var message: Message)

data class ResponseWrapper(
    var read: ReadResponse?,
    var write: WriteResponse?,
    var messages: List<MessageResponse>?
) : Serializable
