class Printer() {
    val indent = "  "

    init {
        instance = this
    }

    companion object {
        var instance: Printer? = null
        set(value) {
            if (field != null) {
                throw IllegalArgumentException()
            }
            field = value
        }
    }

    /**
     * Prints in console command result
     *
     * @param text source text
     */
    fun printCommandResult(text: String) = println(text)

    /**
     * Prints in console Exception message
     *
     * @param exception source exception
     */
    fun printError(exception: Exception) = println("Error: ${exception.message}")

    /**
     * Requests input from user in console
     *
     * @param text hint for user
     * @return result
     */
    fun makeInputRequest(text: String): String? {
        print("$indent$text: ")
        val s: String? = readLine()
        return if (s == "") null else s
    }

    /**
     * Requests to type a command from user
     *
     * @return result
     */
    fun makeCommandRequest() : String = readLine()!!
}
