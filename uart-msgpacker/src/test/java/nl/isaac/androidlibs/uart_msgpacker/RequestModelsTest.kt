package nl.isaac.androidlibs.uart_msgpacker

import nl.isaac.androidlibs.uart_msgpacker.gson.GsonUtil
import nl.isaac.androidlibs.uart_msgpacker.model.*
import nl.isaac.androidlibs.uart_msgpacker.packer.stringToBytes
import nl.isaac.androidlibs.uart_msgpacker.packer.toHexString
import org.junit.Test
import org.msgpack.core.MessagePack

class RequestModelsTest {

    @Test
    fun testReadRequest() {
        // Values for request.
        val modbusIdList = listOf(41000, 41001, 41002, 41003)
        val rid = 2
        val request = ReadRequest(rid, modbusIdList.toTypedArray())
        // Pack request
        val packedRequest = request.packRequest(false)
        val hex = packedRequest.toHexString()
        // Unpack request to new object.
        val requestParsed = MessagePack.newDefaultUnpacker(hex.stringToBytes()).unpackValue().toJson()
        val json = GsonUtil.getGson().fromJson<ReadRequest>(requestParsed, ReadRequest::class.java)

        // Assertions
        assert(request.read.size == 4)
        assert(packedRequest.isNotEmpty())
        assert(json.rid == rid)
        assert(json.read.size == modbusIdList.size)
        assert(json.read.contentEquals(modbusIdList.toTypedArray()))
    }

    @Test
    fun testWriteRequest() {
        // Values for request.
        val writeMap = mapOf(41000 to 1, 41022 to "BoosterControl", 41005 to 1.0)
        val rid = 3
        val request = WriteRequest(rid, writeMap as Map<Any, Any?>)

        // Pack request
        val packedRequest = request.packRequest(false)
        // Unpack to new object
        val unpackedRequest = MessagePack.newDefaultUnpacker(packedRequest).unpackValue().toJson()
        val json = GsonUtil.getGson().fromJson<WriteRequest>(unpackedRequest, WriteRequest::class.java)

        // Assertions
        assert(request.write.size == writeMap.size)
        assert(packedRequest.isNotEmpty())
        assert(json.rid == rid)
        assert(json.write.size == writeMap.size)
        assert(writeMap.all { json.write.containsKey(it.key) })
        assert(writeMap.all { json.write[it.key] == it.value })
    }


    @Test
    fun testResetAllMessagesRequest(){
        // Values for request.
        val rid = 34
        val request = ResetAllMessagesRequest(rid)
        // Pack request
        val packedRequest = request.packRequest(false)
        // Unpack to new object
        val unpackedRequest = MessagePack.newDefaultUnpacker(packedRequest).unpackValue().toJson()
        val json = GsonUtil.getGson().fromJson<ResetAllMessagesRequest>(unpackedRequest, ResetAllMessagesRequest::class.java)

        // Assertions
        assert(packedRequest.isNotEmpty())
        assert(json.rid == rid)
        assert(json.resetModbusRegister == request.resetModbusRegister)
    }

    @Test
    fun testResetMessagesByIDRequest(){
        // Values for request.
        val rid = 2435
        val messageIds = arrayOf(801, 802, 101, 201, 301)
        val request = ResetMessagesByIdRequest(rid, messageIds as Array<Any>)
        // Pack request
        val packedRequest = request.packRequest(false)
        // Unpack to new object
        val unpackedRequest = MessagePack.newDefaultUnpacker(packedRequest).unpackValue().toJson()
        val json = GsonUtil.getGson().fromJson<ResetMessagesByIdRequest>(unpackedRequest, ResetMessagesByIdRequest::class.java)

        // Assertions
        assert(packedRequest.isNotEmpty())
        assert(json.rid == rid)
        assert(json.messageIDs.contentEquals(request.messageIDs))
    }

    @Test
    fun testGetAllMessagesRequest(){
        // Values for request.
        val rid = 42
        val request = GetMessagesRequest(rid)
        // Pack request
        val packedRequest = request.packRequest(false)
        // Unpack to new object
        val unpackedRequest = MessagePack.newDefaultUnpacker(packedRequest).unpackValue().toJson()
        val json = GsonUtil.getGson().fromJson<GetMessagesRequest>(unpackedRequest, GetMessagesRequest::class.java)

        // Assertions
        assert(packedRequest.isNotEmpty())
        assert(json.rid == rid)
        assert(json.readMessages.timestamp.contentEquals(request.readMessages.timestamp))
        assert(json.readMessages.count == request.readMessages.count)
    }

    @Test
    fun testGetSomeMessagesRequest(){
        // Values for request.
        val rid = 42
        val count = 52345
        val timestamp = arrayOf(1, 23, 42, 12, 52, 34)
        val request = GetMessagesRequest(rid, ReadMessages(timestamp, count))
        // Pack request
        val packedRequest = request.packRequest(false)
        // Unpack to new object
        val unpackedRequest = MessagePack.newDefaultUnpacker(packedRequest).unpackValue().toJson()
        val json = GsonUtil.getGson().fromJson<GetMessagesRequest>(unpackedRequest, GetMessagesRequest::class.java)

        // Assertions
        assert(packedRequest.isNotEmpty())
        assert(json.rid == rid)
        assert(json.readMessages.timestamp.contentEquals(request.readMessages.timestamp))
        assert(json.readMessages.count == request.readMessages.count)
    }

    @Test
    fun testGetAllLogsRequest(){
        // Values for request.
        val rid = 16
        val request = GetLogsRequest(rid)
        // Pack request
        val packedRequest = request.packRequest(false)
        // Unpack to new object
        val unpackedRequest = MessagePack.newDefaultUnpacker(packedRequest).unpackValue().toJson()
        val json = GsonUtil.getGson().fromJson(unpackedRequest, GetLogsRequest::class.java)

        // Assertions
        assert(packedRequest.isNotEmpty())
        assert(json.rid == rid)
        assert(json.readLog.timestamp.contentEquals(request.readLog.timestamp))
        assert(json.readLog.count == request.readLog.count)
    }

    @Test
    fun testGetSomeLogsRequest(){
        // Values for request.
        val rid = 16
        val count = 2
        val timestamp = arrayOf(22, 12, 15, 11, 41, 26)
        val request = GetLogsRequest(rid, ReadLog(timestamp, count))
        // Pack request
        val packedRequest = request.packRequest(false)
        // Unpack to new object
        val unpackedRequest = MessagePack.newDefaultUnpacker(packedRequest).unpackValue().toJson()
        val json = GsonUtil.getGson().fromJson(unpackedRequest, GetLogsRequest::class.java)

        // Assertions
        assert(packedRequest.isNotEmpty())
        assert(json.rid == rid)
        assert(json.readLog.timestamp.contentEquals(request.readLog.timestamp))
        assert(json.readLog.count == request.readLog.count)
    }
}

