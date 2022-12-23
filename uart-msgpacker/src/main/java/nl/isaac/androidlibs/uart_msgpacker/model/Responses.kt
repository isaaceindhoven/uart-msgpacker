package nl.isaac.androidlibs.uart_msgpacker.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable



enum class TimestampType(val id: String) {
    OCCURRED("occured"), RESET("reset"), ROOTCAUSEDISAPPEARED("rootcausedisappeared"), END_OF_LIST("endoflist")
}

enum class PumpMode(val id: Int) {
    AUTOMATIC(0), OFF(1), MANUAL(2)
}

enum class PumpLoadsType{
    ACTIVE, OFF
}

data class ReadResponse(var rid: Int, var read: Map<Int, Any?>) : Serializable

data class WriteResponse(var rid: Int, var result: Int?, var write: Map<Int, Any?>) : Serializable

data class ResetMessagesResponse(var rid: Int, var resetMessages: Map<Int, Any?>) : Serializable

data class LogEntries(
    var timestamp: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0),
    var dispress: Int? = null,
    var sucpress: Int?= null,
    var tankpress: Int?= null,
    var setbms: Int?= null,
    var setdisp: Int?= null,
    var bandw: Int?= null,
    var temp: Int?= null,
    var rdp: Int?= null,
    var tankrel: Int?= null,
    var propvalve: Int?= null,
    var altpropvalve: Int?= null,
    var solvalve: Boolean?= null,
    var altsolvalve: Boolean?= null,
    var pumpmodes: Array<PumpMode>?= null,
    var pumpfails: Array<Boolean>?= null,
    var pumploads: Int?= null,
    var msgocc: Int?= null,
    var msgrcd: Int?= null,
    var msgres: Int?= null,
    var extonoff: Int?= null,
    var firealarm: Int?= null,
    var resetall: Int?= null,
    var altsetp: Int?= null,
    var checkrun: Int?= null,
    var emrgpow: Int?= null,
    var flush: Int?= null,
    var failsupvalve: Int?= null,
    var failaddsupvalve: Int?= null,
) {
    fun getPumpLoadsStatus(): PumpLoadsType {
        return when {
            pumploads != 255 -> PumpLoadsType.ACTIVE
            else -> PumpLoadsType.OFF
        }
    }

    fun getCurrentTimestamp(): Array<Int> {
        return timestamp
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LogEntries

        if (!timestamp.contentEquals(other.timestamp)) return false
        if (dispress != other.dispress) return false
        if (sucpress != other.sucpress) return false
        if (tankpress != other.tankpress) return false
        if (setbms != other.setbms) return false
        if (setdisp != other.setdisp) return false
        if (bandw != other.bandw) return false
        if (temp != other.temp) return false
        if (rdp != other.rdp) return false
        if (tankrel != other.tankrel) return false
        if (propvalve != other.propvalve) return false
        if (altpropvalve != other.altpropvalve) return false
        if (solvalve != other.solvalve) return false
        if (altsolvalve != other.altsolvalve) return false
        if (pumpmodes != null) {
            if (other.pumpmodes == null) return false
            if (!pumpmodes.contentEquals(other.pumpmodes)) return false
        } else if (other.pumpmodes != null) return false
        if (pumpfails != null) {
            if (other.pumpfails == null) return false
            if (!pumpfails.contentEquals(other.pumpfails)) return false
        } else if (other.pumpfails != null) return false
        if (pumploads != other.pumploads) return false
        if (msgocc != other.msgocc) return false
        if (msgrcd != other.msgrcd) return false
        if (msgres != other.msgres) return false
        if (extonoff != other.extonoff) return false
        if (firealarm != other.firealarm) return false
        if (resetall != other.resetall) return false
        if (altsetp != other.altsetp) return false
        if (checkrun != other.checkrun) return false
        if (emrgpow != other.emrgpow) return false
        if (flush != other.flush) return false
        if (failsupvalve != other.failsupvalve) return false
        if (failaddsupvalve != other.failaddsupvalve) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.contentHashCode()
        result = 31 * result + (dispress ?: 0)
        result = 31 * result + (sucpress ?: 0)
        result = 31 * result + (tankpress ?: 0)
        result = 31 * result + (setbms ?: 0)
        result = 31 * result + (setdisp ?: 0)
        result = 31 * result + (bandw ?: 0)
        result = 31 * result + (temp ?: 0)
        result = 31 * result + (rdp ?: 0)
        result = 31 * result + (tankrel ?: 0)
        result = 31 * result + (propvalve ?: 0)
        result = 31 * result + (altpropvalve ?: 0)
        result = 31 * result + (solvalve?.hashCode() ?: 0)
        result = 31 * result + (altsolvalve?.hashCode() ?: 0)
        result = 31 * result + (pumpmodes?.contentHashCode() ?: 0)
        result = 31 * result + (pumpfails?.contentHashCode() ?: 0)
        result = 31 * result + (pumploads ?: 0)
        result = 31 * result + (msgocc ?: 0)
        result = 31 * result + (msgrcd ?: 0)
        result = 31 * result + (msgres ?: 0)
        result = 31 * result + (extonoff ?: 0)
        result = 31 * result + (firealarm ?: 0)
        result = 31 * result + (resetall ?: 0)
        result = 31 * result + (altsetp ?: 0)
        result = 31 * result + (checkrun ?: 0)
        result = 31 * result + (emrgpow ?: 0)
        result = 31 * result + (flush ?: 0)
        result = 31 * result + (failsupvalve ?: 0)
        result = 31 * result + (failaddsupvalve ?: 0)
        return result
    }
}

data class Message(var mid: Int,
                   @SerializedName("endoflist")
                   var endOfList: Array<Int>?,
                   @SerializedName("occured")
                   var occured: Array<Int>?,
                   @SerializedName("reset")
                   var reset: Array<Int>?,
                   @SerializedName("rootcausedisappeared")
                   var rootcause: Array<Int>?) {

    fun getType() : TimestampType {
        return when {
            occured != null -> TimestampType.OCCURRED
            reset != null -> TimestampType.RESET
            rootcause != null -> TimestampType.ROOTCAUSEDISAPPEARED
            endOfList != null -> TimestampType.END_OF_LIST
            else -> throw RuntimeException("Unknown message is being created")
        }
    }

    fun getTimestamp() : Array<Int> {
        val default = Array<Int>(6) { 0 }
        return when (getType()) {
            TimestampType.OCCURRED -> occured ?: default
            TimestampType.RESET -> reset ?: default
            TimestampType.ROOTCAUSEDISAPPEARED -> rootcause ?: default
            TimestampType.END_OF_LIST -> endOfList ?: default
        }
    }

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

data class LogResponse(var rid: Int, var logEntries: LogEntries)

data class ResponseWrapper(
    var read: ReadResponse?,
    var write: WriteResponse?,
    var messages: List<MessageResponse>?,
    var logs: List<LogResponse>?
) : Serializable
