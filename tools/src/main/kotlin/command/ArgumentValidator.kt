package command

import exceptions.InvalidArgumentsForCommandException
import java.lang.NullPointerException

class ArgumentValidator(private val argumentTypes: List<ArgumentType>) {
    init {
        if (argumentTypes != argumentTypes.sorted())
            throw IllegalArgumentException("Описание аргументов команды должно идти в порядке: INT -> LONG -> " +
                    "FLOAT -> DOUBLE -> STRING -> ORGANIZATION.\n" +
                    "Обратитесь к разработчику для разъяснения ситуации: dakako@go4rta.com")
    }

    fun check(args: CommandArgument) {
        var organizationCounter = 0
        var counter = 0
        for (type in argumentTypes) {
            try {
                when (type) {
                    ArgumentType.INT -> args.primitiveTypeArguments?.get(counter)!!.toInt()
                    ArgumentType.LONG -> args.primitiveTypeArguments?.get(counter)!!.toLong()
                    ArgumentType.FLOAT -> args.primitiveTypeArguments?.get(counter)!!.toFloat()
                    ArgumentType.DOUBLE -> args.primitiveTypeArguments?.get(counter)!!.toDouble()
                    ArgumentType.STRING -> args.primitiveTypeArguments?.get(counter)!!.toString()
                    ArgumentType.ORGANIZATION -> { organizationCounter++; counter--}
                }
            } catch (ex: NumberFormatException) {
                throw InvalidArgumentsForCommandException("${args.primitiveTypeArguments?.get(counter) ?: ""}: " +
                        "аргумент не удовлетворяет условию type=$type")
            } catch (ex: NullPointerException) {
                throw InvalidArgumentsForCommandException(("Аргумент $type - не найден"))
            }
            counter++
        }

        if (counter != (args.primitiveTypeArguments?.size ?: 0))
            throw InvalidArgumentsForCommandException(
                if (organizationCounter != 0 && args.organizations.size == 0)
                    "аргумент - объект класса Organization - вводится на следующих строках " +
                            "(для ввода объекта в конце строки поставьте \\"
                else
                    "${args.primitiveTypeArguments?.get(counter)}: неизвестный аргумент")
        if (organizationCounter > args.organizations.size)
            throw InvalidArgumentsForCommandException("Аргумент ORGANIZATION: не найден, " +
                    "для ввода объекта в конце строки поставьте \\")
        else if (organizationCounter < args.organizations.size)
            throw InvalidArgumentsForCommandException("Неизвестный аргумент ORGANIZATION")
    }
}