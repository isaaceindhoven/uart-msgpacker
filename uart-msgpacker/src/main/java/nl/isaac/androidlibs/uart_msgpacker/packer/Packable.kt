package nl.isaac.androidlibs.uart_msgpacker.packer

interface Packable {
    val rid: Int
    fun packRequest(shouldUseNewKeys: Boolean): ByteArray
}