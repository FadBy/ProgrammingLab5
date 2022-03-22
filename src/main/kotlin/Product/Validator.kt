import Exceptions.IncorrectInputException

abstract class Validator<T> {
    abstract val child: Validator<T>?
    abstract val exception: IncorrectInputException

    open fun checkValidness(value: T?) {
        if (value == null) {
            throw IllegalArgumentException("Null is not checked")
        }
        child?.checkValidness(value)
    }

    protected fun throwException() {
        throw exception
    }
}

class NotNullValidator<T>(override val child: Validator<T>?, override val exception: IncorrectInputException) :
    Validator<T>() {
    constructor(child: Validator<T>?, exceptionText: String) : this(child, IncorrectInputException(exceptionText))
    constructor(child: Validator<T>?) : this(child, IncorrectInputException())
    constructor(exceptionText: String) : this(null, IncorrectInputException(exceptionText))
    constructor() : this(null, IncorrectInputException())

    override fun checkValidness(value: T?) {
        if (value == null) {
            throwException()
        }
        super.checkValidness(value)
    }
}

class NullableValidator<T>(override val child: Validator<T>?, override val exception: IncorrectInputException) : Validator<T>() {
    constructor(child: Validator<T>?, exceptionText: String) : this(child, IncorrectInputException(exceptionText))
    constructor(child: Validator<T>?) : this(child, IncorrectInputException())
    constructor(exceptionText: String) : this(null, IncorrectInputException(exceptionText))
    constructor() : this(null, IncorrectInputException())

    override fun checkValidness(value: T?) {
        if (value != null) {
            super.checkValidness(value)
        }
    }
}

open class CompareToNumberValidator<T : Number>(
    override val child: Validator<T>?,
    override val exception: IncorrectInputException,
    val number: T,
    val more: Boolean,
    val equals: Boolean
) : Validator<T>() {

    constructor(
        child: Validator<T>?,
        exceptionText: String,
        number: T,
        more: Boolean,
        equals: Boolean
    ) : this(child, IncorrectInputException(exceptionText), number, more, equals)

    constructor(child: Validator<T>?, number: T, more: Boolean, equals: Boolean) : this(
        child,
        IncorrectInputException(),
        number,
        more,
        equals
    )

    constructor(exceptionText: String, number: T, more: Boolean, equals: Boolean) : this(
        null,
        IncorrectInputException(exceptionText),
        number,
        more,
        equals
    )

    constructor(number: T, more: Boolean, equals: Boolean) : this(
        null,
        IncorrectInputException(),
        number,
        more,
        equals
    )

    override fun checkValidness(value: T?) {
        super.checkValidness(value)
        val number1: Double = number.toDouble()
        val value1: Double = value!!.toDouble()
        if (more) {
            if (equals) {
                if (value1 < number1)
                    throwException()
            } else {
                if (value1 <= number1)
                    throwException()
            }
        } else {
            if (equals) {
                if (value1 > number1)
                    throwException()
            } else {
                if (value1 >= number1)
                    throwException()
            }
        }
    }
}

class MoreThanZeroValidator<T : Number>(override val child: Validator<T>?, override val exception: IncorrectInputException) :
    CompareToNumberValidator<T>(child, exception, 0 as T, true, false) {
    constructor(child: Validator<T>?) : this(child, IncorrectInputException())
    constructor(textException: String) : this(null, IncorrectInputException(textException))
    constructor(child: Validator<T>?, textException: String) : this(child, IncorrectInputException(textException))
    constructor() : this(null, IncorrectInputException())
}

class UniqueValidator<T>(
    override val child: Validator<T>?,
    override val exception: IncorrectInputException,
    private val valuesSet: Set<T>
) : Validator<T>() {
    constructor(child: Validator<T>?, exceptionText: String, valuesSet: Set<T>) : this(child, IncorrectInputException(exceptionText), valuesSet)
    constructor(child: Validator<T>?, valuesSet: Set<T>) : this(child, IncorrectInputException(), valuesSet)
    constructor(exceptionText: String, valuesSet: Set<T>) : this(null, IncorrectInputException(exceptionText), valuesSet)
    constructor(valuesSet: Set<T>) : this(null, IncorrectInputException(), valuesSet)

    override fun checkValidness(value: T?) {
        super.checkValidness(value)
        if (value in valuesSet) {
            throwException()
        }
    }
}

class NotEmptyValidator(override val child: Validator<String?>?, override val exception: IncorrectInputException) :
    Validator<String?>() {
    constructor(child: Validator<String?>?, exceptionText: String) : this(child, IncorrectInputException(exceptionText))
    constructor(child: Validator<String?>?) : this(child, IncorrectInputException())
    constructor(exceptionText: String) : this(null, IncorrectInputException(exceptionText))
    constructor() : this(null, IncorrectInputException())

    override fun checkValidness(value: String?) {
        super.checkValidness(value)
        if (value == "") {
            throwException()
        }
    }
}