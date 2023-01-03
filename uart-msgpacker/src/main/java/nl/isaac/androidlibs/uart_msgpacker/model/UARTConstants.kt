package nl.isaac.androidlibs.uart_msgpacker.model

enum class UARTConstants(val serializationKey: String) {
    RID("rid"),
    READ("read"),
    WRITE("write"),
    READ_MESSAGES("readMessages"),
    RESET_MESSAGES("resetMessages"),
    READ_LOG("readLog"),
    COUNT("count"),
    TIMESTAMP("timestamp")
}