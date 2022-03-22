import Exceptions.IncorrectCommandException
import Exceptions.IncorrectInputException
import Product.Organization
import com.google.gson.JsonArray
import java.lang.IndexOutOfBoundsException
import java.lang.NumberFormatException
import java.util.*

class Commander(val commands: Map<String, Command>) {
    abstract class Command() {
        abstract fun act(args: List<String>)
    }

    class HelpCommand() : Command() {
        override fun act(args: List<String>) {
            Printer.instance!!.printCommandResult(
                "help : вывести справку по доступным командам\n" +
                        "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                        "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                        "add {element} : добавить новый элемент в коллекцию\n" +
                        "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                        "remove_by_id id : удалить элемент из коллекции по его id\n" +
                        "clear : очистить коллекцию\n" +
                        "save : сохранить коллекцию в файл\n" +
                        "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                        "exit : завершить программу (без сохранения в файл)\n" +
                        "add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                        "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n" +
                        "history : вывести последние 15 команд (без их аргументов)\n" +
                        "count_less_than_manufacturer manufacturer : вывести количество элементов, значение поля manufacturer которых меньше заданного\n" +
                        "print_ascending : вывести элементы коллекции в порядке возрастания\n" +
                        "print_field_descending_manufacturer : вывести значения поля manufacturer всех элементов в порядке убывания"
            )
        }
    }

    class InfoCommand(val holder: CollectionHolder) : Command() {
        override fun act(args: List<String>) {
            Printer.instance!!.printCommandResult(
                "CollectionType: ${holder.type}\n" +
                        "InitializationDate: ${holder.creationDate}\n" +
                        "Size: ${holder.size}"
            )
        }
    }

    class ShowCommand(val holder: CollectionHolder) : Command() {
        override fun act(args: List<String>) {
            val iter = holder.iterator()
            while (iter.hasNext()) {
                val text: String = iter.next().toString()
                Printer.instance!!.printCommandResult(text + (if (iter.hasNext()) "\n" else ""))
            }
        }
    }

    class AddCommand(val holder: CollectionHolder) : Command() {
        override fun act(args: List<String>) {
            holder.add()
        }
    }

    class UpdateIdCommand(val holder: CollectionHolder, val add: AddCommand, val remove: RemoveByIdCommand) : Command() {
        override fun act(args: List<String>) {
            if (args.isEmpty()) {
                throw IncorrectCommandException("Arguments are empty")
            }
            val id: Long = args[0].toLongOrNull() ?: throw IncorrectInputException("id must be Long")
            val product = holder.findById(id) ?: throw IncorrectInputException("Such id not found")
            remove.act(listOf(id.toString()))
            add.act(listOf())
        }
    }

    class RemoveByIdCommand(val holder: CollectionHolder) : Command() {
        override fun act(args: List<String>) {
            try {
                holder.removeById(args[0].toLong())
            } catch (e: IndexOutOfBoundsException) {
                throw IncorrectCommandException("You have to type an argument")
            } catch (e: NumberFormatException) {
                throw IncorrectCommandException("You have to type a number")
            }
        }
    }

    class ClearCommand(val holder: CollectionHolder) : Command() {
        override fun act(args: List<String>) {
            holder.clear()
        }
    }

    class SaveCommand(val holder: CollectionHolder, val data: Data) : Command() {
        override fun act(args: List<String>) {
            val writer = data.getWriter()
            val arr = JsonArray()
            for (el in holder) {
                arr.add(el.asJsonObject)
            }
            writer.write(arr.toString())
            writer.flush()
        }
    }

    class ExecuteScriptCommand() : Command() {
        private lateinit var map: Map<String, Command>

        override fun act(args: List<String>) {
            if (args.size == 0) {
                throw IncorrectCommandException("Arguments are empty")
            }
            val reader = Data.getReader(args[0])
            val commands = reader.readLines()
            for (i in commands) {
                val separatedText = i.split(" ")
                val command = separatedText[0]
                var arguments: List<String>
                if (separatedText.size > 1) {
                    arguments = separatedText.slice(1 until separatedText.size)
                } else {
                    arguments = listOf()
                }
                if (command !in map) {
                    throw IncorrectCommandException("$command - this command doesn't exist")
                }
                map[command]?.act(arguments)
            }
        }

        fun setMap(map: Map<String, Command>) {
            this.map = map
        }
    }

    class ExitCommand(val application: Application) : Command() {
        override fun act(args: List<String>) {
            application.exit()
        }
    }

    class AddIfMinCommand(val holder: CollectionHolder, val addCommand: AddCommand, val removeByIdCommand: RemoveByIdCommand) : Command() {
        override fun act(args: List<String>) {
            val product = holder.add()
            val min = holder.getMin()
            if (min < product) {
                holder.removeById(product.id)
            }
        }
    }

    class RemoveLowerCommand(val holder: CollectionHolder) : Command() {
        override fun act(args: List<String>) {
            val product = holder.add()
            holder.removeAll(holder.headSet(product))
        }
    }

    class HistoryCommand(val history: List<String>) : Command() {
        override fun act(args: List<String>) {
            val iter = history.iterator()
            for (i in 1..15) {
                if (!iter.hasNext()) {
                    return
                } else {
                    Printer.instance!!.printCommandResult(iter.next())
                }
            }
        }
    }

    class CountLessThanManufacturerCommand(val holder: CollectionHolder) : Command() {
        override fun act(args: List<String>) {
            if (args.size == 0) {
                throw IncorrectCommandException("Arguments are empty")
            }
            val organizationBuilder = Organization.Builder()
            val printer = Printer.instance!!
            var i = 1
            while (true) {
                try {
                    when (i) {
                        1 -> organizationBuilder.setName(printer.makeInputRequest("Product.Organization.name (String)"))
                        2 -> organizationBuilder.setFullName(printer.makeInputRequest("Product.Organization.fullName (String)"))
                        3 -> organizationBuilder.setAnnualTurnover(
                            printer.makeInputRequest(
                                "Product.Organization.annualTurnover (Long)",
                            )
                        )
                        4 -> {
                            organizationBuilder.setEmployeesCount(
                                printer.makeInputRequest(
                                    "Product.Organization.EmployeesCount (Long)",
                                )
                            )
                        }
                        else -> break
                    }
                    i += 1
                } catch (e: IncorrectInputException) {
                    if (e.message != null) {
                        printer.printCommandResult(e.message)
                    }
                }
            }
            val manufacturer = organizationBuilder.build()
            var count = 0
            for (el in holder.getAscendingOrder()) {
                if (el.manufacturer == null || manufacturer.compareTo(el.manufacturer) > 0) {
                    count++
                }
            }
            printer.printCommandResult(count.toString())
        }
    }

    class PrintAscendingCommand(val holder: CollectionHolder) : Command() {
        override fun act(args: List<String>) {
            for (i in holder.getAscendingOrder()) {
                Printer.instance!!.printCommandResult(i.toString() + "\n")
            }
        }
    }

    class PrintFieldDescendingManufacturerCommand(val holder: CollectionHolder) : Command() {
        override fun act(args: List<String>) {
            val organizations = TreeSet<Organization>()
            for (i in holder.getAscendingOrder()) {
                if (i.manufacturer != null)
                    organizations.add(i.manufacturer)
            }
            for (i in organizations.descendingIterator()) {
                Printer.instance!!.printCommandResult(i.toString() + "\n")
            }
        }
    }

    fun runCommand(command: String, arguments: List<String>) {
        if (command !in commands)
            throw IncorrectCommandException("No such command. Type \"help\" to see all commands")
        commands[command]!!.act(arguments)
    }
}