import Exceptions.ApplicationException
import Product.Product
import com.google.gson.JsonArray
import com.google.gson.JsonParser

class Application() {
    private var collectionPath: String = System.getenv("ProgLab")
    private val data = Data(collectionPath)
    private val collectionHolder: CollectionHolder = loadCollectionHolder()
    private var ran = false
    private val printer = Printer()
    private val history = ArrayList<String>()
    private val commands = mapOf(
        "help" to Commander.HelpCommand(),
        "info" to Commander.InfoCommand(collectionHolder),
        "show" to Commander.ShowCommand(collectionHolder),
        "add" to Commander.AddCommand(collectionHolder),
        "update_id" to Commander.UpdateIdCommand(
            collectionHolder,
            Commander.AddCommand(collectionHolder),
            Commander.RemoveByIdCommand(collectionHolder)
        ),
        "remove_by_id" to Commander.RemoveByIdCommand(collectionHolder),
        "clear" to Commander.ClearCommand(collectionHolder),
        "save" to Commander.SaveCommand(collectionHolder, data),
        "execute_script" to Commander.ExecuteScriptCommand(),
        "exit" to Commander.ExitCommand(this),
        "add_if_min" to Commander.AddIfMinCommand(
            collectionHolder,
            Commander.AddCommand(collectionHolder),
            Commander.RemoveByIdCommand(collectionHolder)
        ),
        "remove_lower" to Commander.RemoveLowerCommand(collectionHolder),
        "history" to Commander.HistoryCommand(history),
        "count_less_than_manufacturer" to Commander.CountLessThanManufacturerCommand(collectionHolder),
        "print_ascending" to Commander.PrintAscendingCommand(collectionHolder),
        "print_field_descending_manufacturer" to Commander.PrintFieldDescendingManufacturerCommand(collectionHolder)
    )
    private val commander = Commander(commands)

    init {
        (commands["execute_script"] as Commander.ExecuteScriptCommand).setMap(commands)
    }

    private var exitState = false

    /**
     * Starts main application
     */
    fun run() {
        if (ran) {
            throw ApplicationException("Application already runned")
        }
        ran = true
        start()
        programCycle()
        destroy()
    }

    /**
     * Makes preparations before main cycle
     */
    private fun start() {

    }

    /**
     * Main cycle
     */
    private fun programCycle() {
        while (!exitState) {
            try {
                val separatedText = printer.makeCommandRequest().split(' ')
                val command = separatedText[0]
                var arguments = listOf<String>()
                if (separatedText.size > 1) {
                    arguments = separatedText.slice(1 until separatedText.size)
                }
                commander.runCommand(command, arguments)
                if (command != "history" && command != "help") {
                    history.add(command)
                }
            } catch (e: Exception) {
                printer.printError(e)
            }
        }
    }

    /**
     * does some stuff before closing a program
     */
    private fun destroy() {

    }

    /**
     *
     */
    private fun loadCollectionHolder(): CollectionHolder {
        val holder = CollectionHolder()
        val jsonElement = JsonParser.parseReader(data.getReader())
        var jsonArray = JsonArray()
        if (!jsonElement.isJsonNull) {
            jsonArray = jsonElement.asJsonArray
        }
        val partNumbers = HashSet<String?>()
        val ids = HashSet<Long>()
        for (el in jsonArray) {
            val product = Product.createByJsonObject(el.asJsonObject, partNumbers, ids)
            holder.add(product)
            partNumbers.add(product.partNumber)
            ids.add(product.id)
        }
        return holder
    }

    fun exit() {
        exitState = true
    }
}