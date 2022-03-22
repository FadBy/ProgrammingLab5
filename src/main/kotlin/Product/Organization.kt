package Product

import Exceptions.IncorrectInputException
import MoreThanZeroValidator
import NotEmptyValidator
import NotNullValidator
import UniqueValidator
import com.google.gson.JsonObject
import java.lang.NumberFormatException
import java.util.*

class Organization private constructor(
    var name: String?,
    var fullname: String?,
    var annualTurnover: Long?,
    var employeesCount: Long?,
    var id: Int
) : Comparable<Organization>, Jsonable {

    override val asJsonObject: JsonObject
    get() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(nameString, name)
        jsonObject.addProperty(fullNameString, fullname)
        jsonObject.addProperty(annualTurnoverString, annualTurnover)
        jsonObject.addProperty(employeesCountString, employeesCount)
        jsonObject.addProperty(idString, id)
        return jsonObject
    }

    companion object {
        private const val nameString = "Name"
        private const val fullNameString = "FullName"
        private const val annualTurnoverString = "AnnualTurnover"
        private const val employeesCountString = "EmployeesCount"
        private const val idString = "Id"

        fun createByJsonObject(values: JsonObject, ids: Set<Int>) : Organization {
            val builder = Builder()
            builder.setName(values.getAsJsonPrimitive(nameString).asString)
            builder.setId(values.getAsJsonPrimitive(idString).asString, ids)
            builder.setEmployeesCount(values.getAsJsonPrimitive(employeesCountString).asString)
            builder.setFullName(values.getAsJsonPrimitive(fullNameString).asString)
            builder.setAnnualTurnover(values.getAsJsonPrimitive(annualTurnoverString).asString)
            return builder.build()
        }
    }

    override fun compareTo(other: Organization): Int {
        return if (annualTurnover != null && other.annualTurnover != null) {
            annualTurnover!!.compareTo(other.annualTurnover!!);
        } else if (annualTurnover == null && other.annualTurnover == null) {
            0
        } else if (other.annualTurnover == null) {
            1
        } else {
            -1
        }
    }

    override fun toString(): String {
        return "Product.Organization(name=$name," +
                "Fullname=$fullname," +
                "AnnualTurnover=$annualTurnover," +
                "EmployeesCount=$employeesCount," +
                "Id=$id)"
    }


    class Builder() {
        private var name: String? = null
        private var fullName: String? = null
        private var annualTurnover: Long? = null
        private var employeesCount: Long? = null
        private var id: Int? = null

        fun setName(name: String?) {
            NotNullValidator(NotEmptyValidator("Name can't be empty"), "Name can't be null").checkValidness(name)
            this.name = name
        }

        fun setFullName(fullName: String?) {
            NotNullValidator<String?>("FullName can't be null").checkValidness(fullName)
            this.fullName = fullName
        }

        fun setAnnualTurnover(annualTurnover: String?) {
            val annualTurnover1: Long?
            try {
                annualTurnover1 = annualTurnover?.toLong()
            } catch (e: NumberFormatException) {
                throw IncorrectInputException("AnnualTurnover must be long")
            }
            NotNullValidator(
                MoreThanZeroValidator<Long>("AnnualTurnover must be more than zero"),
                "AnnualTurnover can't be null"
            ).checkValidness(annualTurnover1)
            this.annualTurnover = annualTurnover1
        }

        fun setEmployeesCount(employeesCount: String?) {
            val employeesCount1: Long?
            try {
                employeesCount1 = employeesCount?.toLong()
            } catch (e: NumberFormatException) {
                throw IncorrectInputException("EmployeesCount must be long")
            }
            NotNullValidator(
                MoreThanZeroValidator<Long>("EmployeesCount must be more than zero"),
                "EmployeesCount can't be null"
            ).checkValidness(employeesCount1)
            this.employeesCount = employeesCount1
        }

        fun setId(id: String?, ids: Set<Int>) {
            var id1: Int? = id?.toIntOrNull()
            if (id != null && id1 == null) {
                throw IncorrectInputException("Id can't be null")
            }
            NotNullValidator(
                MoreThanZeroValidator(
                    UniqueValidator(
                        "Such id already exist. They can't be repeated",
                        ids
                    ), "Id must be more than zero"
                ), "Id can't be null"
            ).checkValidness(id1)
            this.id = id1
        }

        fun build(): Organization {
            if (id == null) {
                id = kotlin.math.abs(Objects.hash(name, fullName, annualTurnover, employeesCount))
            }
            return Organization(name, fullName, annualTurnover, employeesCount, id!!)
        }
    }
}
