package nl.isaac.androidlibs.uart_msgpacker.packer

import nl.isaac.androidlibs.uart_msgpacker.gson.GsonUtil
import nl.isaac.androidlibs.uart_msgpacker.model.*
import org.msgpack.core.MessagePack

class UARTUnpacker {
    companion object {
        /**
         * Method for deserializing custom responses.
         * The JSON String and the library's Gson will be returned to the callsite so that the user can do the string -> POJO conversion as they see fit.
         *
         * @param data The ByteArray containing the messagepack formatted response
         * @param type The Class of the response to deserialize to.
         */
        fun <T> unpackToType(data: ByteArray, type: Class<T>): T {
            val json = MessagePack.newDefaultUnpacker(data).unpackValue().toJson()
            return GsonUtil.getGson().fromJson<T>(json, type)
        }

        /**
         * Method for unpacking to a list of a certain type.
         */
        fun <T> unpackToListOfType(data: ByteArray, type: Class<T>): List<T> {
            val list = mutableListOf<T>()
            val gson = GsonUtil.getGson()
            val unpacker = MessagePack.newDefaultUnpacker(data)
            while (unpacker.hasNext()) {
                val unpacked = unpacker.unpackValue().toJson()
                val deserialized = gson.fromJson<T>(unpacked, type)
                list.add(deserialized)
            }
            return list
        }

        fun unpackToResponseWrapper(data: ByteArray, shouldUseNewKeys: Boolean): ResponseWrapper {
            // Unpack a clone of the data to JSON string first so we can identify the type of response message
            val unpacker = MessagePack.newDefaultUnpacker(data.clone())
            val unpackedResponse = unpacker.unpackValue().toJson()
            return when (determineResponseType(unpackedResponse, shouldUseNewKeys)) {
                ResponseType.MESSAGES -> {
                    val messageList = unpackToListOfType(data, MessageResponse::class.java)
                    ResponseWrapper(null, null, messageList, null)
                }
                ResponseType.WRITE -> {
                    val writeResponse = unpackToType(data, WriteResponse::class.java)
                    ResponseWrapper(null, writeResponse, null, null)
                }
                ResponseType.RESET_MESSAGES -> {
                    val reset = unpackToType(data, ResetMessagesResponse::class.java)
                    ResponseWrapper(
                        null,
                        WriteResponse(reset.rid, null, reset.resetMessages),
                        null,
                        null
                    )
                }
                ResponseType.READ -> {
                    val readResponse = unpackToType(data, ReadResponse::class.java)
                    ResponseWrapper(readResponse, null, null, null)
                }
                ResponseType.LOGS -> {
                    val logResponse = unpackToListOfType(data, LogResponse::class.java)
                    ResponseWrapper(null, null, null, logResponse)
                }
                null -> ResponseWrapper(null, null, null, null)
                else -> ResponseWrapper(null, null, null, null)
            }
        }

        private fun determineResponseType(data: String, shouldUseNewKeys: Boolean): ResponseType? {
            return ResponseType.values().map {
                // Map the ResponseType to the index in the return string. -1 if not found
                it to responseTypeIndex(data, shouldUseNewKeys, it)
            }.filter {
                // Filter all the types that were not found
                it.second != -1
            }.minByOrNull {
                // Find the lowest index, this should be the response type
                it.second
            }?.first
        }

        private fun responseTypeIndex(data: String, shouldUseNewKeys: Boolean, type: ResponseType) : Int {
            return data.indexOf(type.idString(shouldUseNewKeys))
        }

        private enum class ResponseType(val idString: String, val newIdString: String) {
            READ("read", "read"), WRITE("write", "write"), RESET_MESSAGES(
                "resetMessages",
                "resetMessages"
            ),
            MESSAGES("message", "M"), LOGS("logEntries", "L");

            fun idString(shouldUseNewKeys: Boolean): String {
                return if (shouldUseNewKeys) newIdString else idString
            }
        }
    }
}