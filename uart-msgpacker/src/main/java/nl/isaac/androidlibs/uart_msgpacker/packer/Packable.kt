package nl.isaac.androidlibs.uart_msgpacker.packer

interface Packable {
    fun packRequest() : ByteArray
}