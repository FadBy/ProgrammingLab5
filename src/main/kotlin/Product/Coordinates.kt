package Product

import CompareToNumberValidator
import Exceptions.IncorrectInputException
import NotNullValidator
import com.google.gson.JsonObject
import java.lang.NumberFormatException
import kotlin.properties.Delegates

class Coordinates private constructor(
    var x: Long?,
    var y: Long
) : Jsonable {
    override val asJsonObject: JsonObject
        get() {
            val jsonObject = JsonObject()
            jsonObject.addProperty("X", x)
            jsonObject.addProperty("Y", y)
            return jsonObject
        }

    override fun toString(): String {
        return "Coordinates(X=$x, Y=$y)"
    }

    class Builder() {
        private var x: Long? = null
        private var y by Delegates.notNull<Long>()
        private val xString = "X"
        private val yString = "Y"

        constructor(jsonObject: JsonObject) : this() {
            setX(jsonObject.getAsJsonPrimitive(xString).asString)
            setY(jsonObject.getAsJsonPrimitive(yString).asString)
        }

        fun setX(x: String?) {
            val x1: Long?
            try {
                x1 = x?.toLong()
            } catch (e: NumberFormatException) {
                throw IncorrectInputException("X must be long")
            }
            NotNullValidator(CompareToNumberValidator<Long>("Maximum of x is 6", 6, more = false, equals = true)).checkValidness(x1)
            this.x = x1
        }

        fun setY(y: String?) {
            val y1: Long?
            try {
                y1 = y?.toLong()
            } catch (e: NumberFormatException) {
                throw IncorrectInputException("Y must be long")
            }
            NotNullValidator<Long?>("Y can't be null").checkValidness(y1)
            this.y = y1!!
        }

        fun build() : Coordinates {
            return Coordinates(x, y)
        }
    }


}
