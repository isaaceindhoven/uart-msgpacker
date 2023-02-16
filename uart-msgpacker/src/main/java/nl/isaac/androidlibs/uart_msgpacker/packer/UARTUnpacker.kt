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
                ResponseType.MESSAGES.get(shouldUseNewKeys) -> {
                    val messageList = unpackToListOfType(data, MessageResponse::class.java)
                    ResponseWrapper(null, null, messageList, null)
                }
                ResponseType.WRITE.get(shouldUseNewKeys) -> {
                    val writeResponse = unpackToType(data, WriteResponse::class.java)
                    ResponseWrapper(null, writeResponse, null, null)
                }
                ResponseType.RESET_MESSAGES.get(shouldUseNewKeys) -> {
                    val reset = unpackToType(data, ResetMessagesResponse::class.java)
                    ResponseWrapper(
                        null,
                        WriteResponse(reset.rid, null, reset.resetMessages),
                        null,
                        null
                    )
                }
                ResponseType.READ.get(shouldUseNewKeys) -> {
                    val readResponse = unpackToType(data, ReadResponse::class.java)
                    ResponseWrapper(readResponse, null, null, null)
                }
                ResponseType.LOGS.get(shouldUseNewKeys) -> {
                    val logResponse = unpackToListOfType(data, LogResponse::class.java)
                    ResponseWrapper(null, null, null, logResponse)
                }
                null -> ResponseWrapper(null, null, null, null)
                else -> ResponseWrapper(null, null, null, null)
            }
        }

        private fun determineResponseType(data: String, shouldUseNewKeys: Boolean): ResponseType? {
            return when {
                data.contains(ResponseType.WRITE.idString(shouldUseNewKeys)) -> ResponseType.WRITE.get(
                    shouldUseNewKeys
                )
                data.contains(ResponseType.RESET_MESSAGES.idString(shouldUseNewKeys)) -> ResponseType.RESET_MESSAGES.get(
                    shouldUseNewKeys
                )
                data.contains(ResponseType.MESSAGES.idString(shouldUseNewKeys)) -> ResponseType.MESSAGES.get(
                    shouldUseNewKeys
                )
                data.contains(ResponseType.READ.idString(shouldUseNewKeys)) -> ResponseType.READ.get(
                    shouldUseNewKeys
                )
                data.contains(ResponseType.LOGS.idString(shouldUseNewKeys)) -> ResponseType.LOGS.get(
                    shouldUseNewKeys
                )
                else -> null
            }
        }

        private enum class ResponseType(val idString: String) {
            READ("read"), WRITE("write"), RESET_MESSAGES("resetMessages"), MESSAGES("message"), LOGS(
                "logEntries"
            ),
            READ_NEW("read"), WRITE_NEW("write"), RESET_MESSAGES_NEW("resetMessages"), MESSAGES_NEW(
                "M"
            ),
            LOGS_NEW("L");

            fun idString(shouldUseNewKeys: Boolean): String {
                return if (shouldUseNewKeys) {
                    when (this) {
                        READ -> READ_NEW.idString
                        WRITE -> WRITE_NEW.idString
                        RESET_MESSAGES -> RESET_MESSAGES_NEW.idString
                        MESSAGES -> MESSAGES_NEW.idString
                        LOGS -> LOGS_NEW.idString
                        else -> this.idString
                    }
                } else this.idString
            }

            fun get(shouldUseNewKeys: Boolean): ResponseType {
                return if (shouldUseNewKeys) {
                    when (this) {
                        READ -> READ_NEW
                        WRITE -> WRITE_NEW
                        RESET_MESSAGES -> RESET_MESSAGES_NEW
                        MESSAGES -> MESSAGES_NEW
                        LOGS -> LOGS_NEW
                        else -> this
                    }
                } else this
            }
        }
    }
}