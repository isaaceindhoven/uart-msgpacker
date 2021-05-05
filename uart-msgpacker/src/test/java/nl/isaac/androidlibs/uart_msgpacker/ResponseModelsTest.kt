package nl.isaac.androidlibs.uart_msgpacker

import nl.isaac.androidlibs.uart_msgpacker.gson.GsonUtil
import nl.isaac.androidlibs.uart_msgpacker.model.ReadResponse
import nl.isaac.androidlibs.uart_msgpacker.model.ResetMessagesResponse
import nl.isaac.androidlibs.uart_msgpacker.model.WriteResponse
import nl.isaac.androidlibs.uart_msgpacker.packer.UARTUnpacker
import nl.isaac.androidlibs.uart_msgpacker.packer.stringToBytes
import org.junit.Assert
import org.junit.Test
import org.msgpack.core.MessagePack
import java.io.Serializable

data class CustomResponse(var mid: Int, var test: String, var properties: Map<Int, Any?>) : Serializable

class ResponseModelsTest {

    @Test
    fun testUnpackingCustomType() {
        val responseHex = "83A36D696401A474657374A6746573744D65AA70726F7065727469657381CDA02801"
        val otherMapEncodedResponse = "DF00000003A36D696401A474657374A6746573744D65AA70726F70657274696573DF00000001CDA02801"
        val pojoFromResponse = UARTUnpacker.unpackToType(responseHex.stringToBytes(), CustomResponse::class.java)
        val otherMapEncodedPojo = UARTUnpacker.unpackToType(otherMapEncodedResponse.stringToBytes(), CustomResponse::class.java)
        assert(pojoFromResponse.mid == 1)
        assert(pojoFromResponse.test == "testMe")
        assert(pojoFromResponse.properties == mapOf(41000 to 1))
        assert(pojoFromResponse == otherMapEncodedPojo)
    }

    @Test
    fun testParseResponseFromString() {
        val smallResponseHex = "82a372696401a47265616481cdbf6901"
        val unpackedRequest = MessagePack.newDefaultUnpacker(smallResponseHex.stringToBytes()).unpackValue().toJson()
        val json = GsonUtil.getGson().fromJson<ReadResponse>(unpackedRequest, ReadResponse::class.java)

        assert(json.rid == 1)
        assert(json.read == mapOf(49001 to 1))
    }

    @Test
    fun testParseMessageResponseWrapper() {
        val testResponse =
            """
                |82a372696402a76d65737361676582a36d6964cd0320a76f63637572656496140101022e24
                |82a372696402a76d65737361676582a36d6964cd0320a5726573657496140101022e24
                |82a372696402a76d65737361676582a36d6964cd0320b4726f6f746361757365646973617070656172656496140101022e24
                |82a372696402a76d65737361676582a36d6964cd03e8a9656e646f666c69737496000000000000
            """.trimMargin().replace("\n", "").replace("\r", "")
        /* In readable json:

          {
            "rid": 2,
            "message": {
              "mid": 800,
              "occured": [20,1,1,2,46,36]
            }
          }
          {
            "rid": 2,
            "message": {
              "mid": 1000,
              "endoflist": [0,0,0,0,0,0]
            }
          }

        */
        val response = UARTUnpacker.unpackToResponseWrapper(testResponse.stringToBytes())
        assert(response.messages?.size == 4)
        assert(response.messages?.filter { it.message.endOfList != null }?.size == 1)
        assert(response.messages?.filter { it.message.occured != null }?.size == 1)
        assert(response.messages?.filter { it.message.reset != null }?.size == 1)
        assert(response.messages?.filter { it.message.rootcause != null }?.size == 1)
    }

    @Test
    fun testWriteResponse() {
        val hexResponse = "83a372696401a5777269746583cdc0fd00cdafd20dcdafd4cd3c60a6726573756c7400"
        val response = UARTUnpacker.unpackToResponseWrapper(hexResponse.stringToBytes())
        val untypedResponse = UARTUnpacker.unpackToType(hexResponse.stringToBytes(), WriteResponse::class.java)
        assert(response.write?.rid == 1)
        assert(response.write?.write?.size == 3)
        assert(response.write?.result == 0)
        assert(response.write?.equals(untypedResponse) == true)
    }

    @Test
    fun testReadResponseWithError() {
        val map = mapOf<Any, Any>(
            49001 to 1,
            49002 to mapOf("error" to 15),
            49003 to 2
        )
        // Read response with an error on one of the modbus registers.
        val hexResponse = "82a372696401a47265616483cdbf6901cdbf6a81a56572726f720fcdbf6b02"
        val response = UARTUnpacker.unpackToResponseWrapper(hexResponse.stringToBytes())
        val untypedResponse = UARTUnpacker.unpackToType(hexResponse.stringToBytes(), ReadResponse::class.java)
        assert(response.read?.rid == 1)
        assert(response.read?.read?.size == 3)
        assert(map.all { response.read?.read?.keys?.contains(it.key) == true })
        assert(map.all { response.read?.read?.values?.contains(it.value) == true })
        assert(response.read?.read?.get(49002) is Map<*,*>)
        assert((response.read?.read?.get(49002) as Map<*, *>)["error"] == 15)
        assert(response.read?.equals(untypedResponse) == true)
    }

    @Test
    fun testResetMessages() {
        val testResponse = "82a372696404ad72657365744d6573736167657381cd032000"
        /*
        In readable text:
        {
          "rid": 4,
          "resetMessages": {
            "800": 0
          }
        }
         */
        val response = UARTUnpacker.unpackToResponseWrapper(testResponse.stringToBytes())
        val untypedResponse = UARTUnpacker.unpackToType(testResponse.stringToBytes(), ResetMessagesResponse::class.java)

        Assert.assertEquals(800, response.write?.write?.keys?.first())
        Assert.assertEquals(0, response.write?.write?.values?.first())
        assert(response.write?.rid?.equals(4) == true)
        assert(response.write?.rid?.equals(untypedResponse.rid) == true)
        assert(response.write?.write?.keys?.first()?.equals(untypedResponse.resetMessages.keys.first()) == true)
        assert(response.write?.write?.values?.first()?.equals(untypedResponse.resetMessages.values.first()) == true)
    }
}