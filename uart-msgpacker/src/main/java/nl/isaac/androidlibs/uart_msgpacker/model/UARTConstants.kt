package nl.isaac.androidlibs.uart_msgpacker.model

enum class UARTConstants(val serializationKey: String, val oldSerializationKey: String) {
    RID("rid", "rid"),
    READ("read", "read"),
    WRITE("write", "write"),
    READ_MESSAGES("readMessages", "readMessages"),
    RESET_MESSAGES("resetMessages", "resetMessages"),
    READ_LOG("readLog", "readLog"),
    COUNT("count", "count"),
    TIMESTAMP("T", "timestamp");

    fun serializationKey(shouldUseNewKeys: Boolean): String {
        return if (shouldUseNewKeys) this.serializationKey else this.oldSerializationKey
    }
}