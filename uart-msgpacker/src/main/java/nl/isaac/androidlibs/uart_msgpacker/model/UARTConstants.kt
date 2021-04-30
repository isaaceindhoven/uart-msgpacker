package nl.isaac.androidlibs.uart_msgpacker.model

internal enum class UARTConstants(val serializationKey: String) {
    RID("rid"),
    READ("read"),
    WRITE("write"),
    READ_MESSAGES("readMessages"),
    RESET_MESSAGES("resetMessages"),
    COUNT("count"),
    TIMESTAMP("timestamp")
}