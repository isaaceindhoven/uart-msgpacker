package nl.isaac.androidlibs.uart_msgpacker.packer

import nl.isaac.androidlibs.uart_msgpacker.gson.GsonUtil
import nl.isaac.androidlibs.uart_msgpacker.model.MessageResponse
import nl.isaac.androidlibs.uart_msgpacker.model.ReadResponse
import nl.isaac.androidlibs.uart_msgpacker.model.ResponseWrapper
import nl.isaac.androidlibs.uart_msgpacker.model.WriteResponse
import org.msgpack.core.MessagePack
import org.msgpack.core.MessageUnpacker

class UARTUnpacker {
    companion object {
        fun unpackResponse(data: ByteArray): ResponseWrapper {
            // Unpack to JSON string first so we can identify the type of response message
            val unpacker = MessagePack.newDefaultUnpacker(data)
            val unpackedResponse = unpacker.unpackValue().toJson()
            return when (determineResponseType(unpackedResponse)) {
                ResponseType.WRITE, ResponseType.RESET_MESSAGES -> unpackWriteResponse(unpackedResponse)
                ResponseType.MESSAGE -> unpackMessageResponse(unpackedResponse, unpacker)
                ResponseType.READ -> unpackReadResponse(unpackedResponse)
                null -> ResponseWrapper(null, null, null)
            }
        }

        private fun unpackMessageResponse(unpacked: String, unpacker: MessageUnpacker): ResponseWrapper {
            val messageList = mutableListOf<MessageResponse>()
            val gson = GsonUtil.getGson()
            var deserialized = gson.fromJson<MessageResponse>(unpacked, MessageResponse::class.java)
            messageList.add(deserialized)
            while (unpacker.hasNext()) {
                val nextUnpackedMessage = unpacker.unpackValue().toJson()
                deserialized = gson.fromJson<MessageResponse>(nextUnpackedMessage, MessageResponse::class.java)
                messageList.add(deserialized)
            }
            return ResponseWrapper(null, null, messageList)
        }

        private fun unpackReadResponse(unpacked: String): ResponseWrapper {
            val deserialized = GsonUtil.getGson().fromJson<ReadResponse>(unpacked, ReadResponse::class.java)
            return ResponseWrapper(deserialized, null, null)
        }

        private fun unpackWriteResponse(unpacked: String): ResponseWrapper {
            val deserialized = GsonUtil.getGson().fromJson<WriteResponse>(unpacked, WriteResponse::class.java)
            return ResponseWrapper(null, deserialized, null)
        }

        private fun determineResponseType(data: String): ResponseType? {
            return when {
                data.contains(ResponseType.WRITE.idString) -> ResponseType.WRITE
                data.contains(ResponseType.RESET_MESSAGES.idString) -> ResponseType.RESET_MESSAGES
                data.contains(ResponseType.MESSAGE.idString) -> ResponseType.MESSAGE
                data.contains(ResponseType.READ.idString) -> ResponseType.READ
                else -> null
            }
        }

        private enum class ResponseType(val idString: String) {
            READ("read"), WRITE("write"), RESET_MESSAGES("resetMessages"), MESSAGE("message")
        }
    }
}