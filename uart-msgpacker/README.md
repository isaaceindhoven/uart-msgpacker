This library is an implementation of MessagePack on UART as is currently in use by some of our clients. 
It uses predefined Requests which you can create and send to your UART controller. 

# Requests

All requests have an 'rid' property, the request identifer. 
When sending read requests, provide an array of modbus registers you want to read. 
When sending write requests, provide a map with the modbus register and the value to write. 
These two do not fully follow strict typing (maps with `Any` parameter typing), as we cannot know beforehand what input type is configured in the controller for a specific modbusRegister. 
It is therefore up to you to populate the data with the correct typing for the register you are trying to read/write. 

Several Message requests are provided and might be custom to our current controller implementation, but feel free to try.

Requests all implement the `Packable` interface, allowing for easy conversion from POJO to MessagePacked ByteArray. 
You can create your own custom requests and implement the interface if you so desire. 
In this case, make sure to always start with packing a map header for the amount of toplevel properties from your request. 
Extension Functions for packing UART maps and arrays have been added for the `MessagePacker` class.

# Responses

Upon receiving a ByteArray notification containing a UART response, call
`UARTUnpacker.unpackResponse(data)`
in order to get the unpacked and deserialized `ResponseWrapper` POJO. 

# Gson NumberTypeAdapter

The NumberTypeAdapter had to be added in order to allow Integers as map keys, as this is common in the responses coming from UART. 
This Adapter needs to be registered for all Array/Map/List types used in the requests. 
In order to keep functionality as expected when creating new requests, please stick to the currently declared types.
Currently the `NumberTypeAdapter` is registered for the following types:
  - `Map<Any, Any>`
  - `Map<Int, Any?>`
  - `Array<Any>`
  - `List<Any>`
