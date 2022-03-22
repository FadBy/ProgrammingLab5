package Product

import Exceptions.IncorrectInputException
import MoreThanZeroValidator
import NotEmptyValidator
import NotNullValidator
import NullableValidator
import UniqueValidator
import com.google.gson.JsonObject
import java.time.LocalDateTime
import java.util.*
import kotlin.math.abs
import kotlin.properties.Delegates

class Product private constructor(
    val name: String,
    val coordinates: Coordinates,
    val price: Double?,
    val partNumber: String?,
    val manufactureCost: Double,
    val unitOfMeasure: UnitOfMeasure?,
    val manufacturer: Organization?,
    val creationDate: LocalDateTime,
    val id: Long
) : Jsonable, Comparable<Product> {

    companion object {
        private const val nameString = "Name"
        private const val coordinatesString = "Coordinates"
        private const val priceString = "Price"
        private const val partNumberString = "PartNumber"
        private const val manufactureCostString = "ManufactureCost"
        private const val unitOfMeasureString = "UnitOfMeasure"
        private const val manufacturerString = "Manufacturer"
        private const val idString = "Id"
        private const val creationDateString = "CreationDate"

        fun createByJsonObject(values: JsonObject, partNumbers: Set<String?>, ids: Set<Long>): Product {
            val builder = Builder()
            val manufacturerIds = HashSet<Int>()
            builder.setName(values.getAsJsonPrimitive(nameString).asString)
            builder.setCoordinates(Coordinates.Builder(values.getAsJsonObject(coordinatesString)).build())
            val price = values.get(priceString)
            if (price.isJsonNull)
                builder.setPrice(null)
            else
                builder.setPrice(values.getAsJsonPrimitive(priceString).asString)
            builder.setPartNumber(values.getAsJsonPrimitive(partNumberString).asString, partNumbers)
            builder.setManufactureCost(values.getAsJsonPrimitive(manufactureCostString).asString)
            val unit = values.get(unitOfMeasureString)
            if (unit.isJsonNull)
                builder.setUnitOfMeasure(null)
            else
                builder.setUnitOfMeasure(values.getAsJsonPrimitive(unitOfMeasureString).asString)
            val manufacturer1 =
                Organization.createByJsonObject(values.getAsJsonObject(manufacturerString), manufacturerIds)
            manufacturerIds.add(manufacturer1.id)
            builder.setManufacturer(manufacturer1)
            builder.setId(values.getAsJsonPrimitive(idString).asString, ids)
            builder.setCreationDate(values.getAsJsonPrimitive(creationDateString).asString)
            return builder.build()
        }
    }

    override val asJsonObject: JsonObject
        get() {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Name", name)
            jsonObject.add("Coordinates", coordinates.asJsonObject)
            jsonObject.addProperty("Price", price)
            jsonObject.addProperty("PartNumber", partNumber)
            jsonObject.addProperty("ManufactureCost", manufactureCost)
            jsonObject.addProperty("UnitOfMeasure", unitOfMeasure?.text)
            jsonObject.add("Manufacturer", manufacturer?.asJsonObject)
            jsonObject.addProperty("Id", id)
            jsonObject.addProperty("CreationDate", creationDate.toString())
            return jsonObject
        }

    override fun toString(): String {
        return "$nameString='$name'\n" +
                "$coordinatesString=$coordinates\n" +
                "$priceString=$price, " +
                "$partNumberString=$partNumber\n" +
                "$manufactureCostString=$manufactureCost\n" +
                "$unitOfMeasureString=$unitOfMeasure\n" +
                "$manufacturerString=$manufacturer\n" +
                "Id=$id\n" +
                "CreationDate=$creationDate"
    }

    class Builder() {
        private lateinit var name: String
        private lateinit var coordinates: Coordinates
        private var price: Double? = null
        private var partNumber: String? = null
        private var manufactureCost by Delegates.notNull<Double>()
        private var unitOfMeasure: UnitOfMeasure? = null
        private var manufacturer: Organization? = null
        private var id: Long? = null
        private var creationDate: LocalDateTime? = null
        private var ids: Set<Long>? = null

        constructor(product: Product, partNumbers: Set<String?>) : this() {
            setName(product.name)
            setCoordinates(product.coordinates)
            setPrice(product.price.toString())
            setPartNumber(product.partNumber, partNumbers)
            setManufactureCost(product.manufactureCost.toString())
            setUnitOfMeasure(product.unitOfMeasure.toString())
        }

        fun setName(name: String?) {
            NotNullValidator(NotEmptyValidator("Name can't be empty."), "Name can't be null").checkValidness(name)
            this.name = name!!
        }

        fun setCoordinates(coordinates: Coordinates?) {
            NotNullValidator<Coordinates?>("Coordinates can't be null").checkValidness(coordinates)
            this.coordinates = coordinates!!
        }

        fun setPrice(price: String?) {
            val price1: Double?
            price1 = price?.toDoubleOrNull()
            if (price != null && price1 == null) {
                throw IncorrectInputException("Price must be Double")
            }
            NullableValidator(MoreThanZeroValidator<Double>("Price can't be below zero"))
            this.price = price1
        }

        fun setPartNumber(partNumber: String?, partNumbers: Set<String?>) {
            NullableValidator(
                NotEmptyValidator(
                    UniqueValidator<String?>(
                        "This partNumber already exist. They can't be repeated.",
                        partNumbers
                    )
                )
            )
            this.partNumber = partNumber
        }

        fun setManufactureCost(manufactureCost: String?) {
            val manufactureCost1: Double?
            manufactureCost1 = manufactureCost?.toDoubleOrNull()
            if (manufactureCost != null && manufactureCost1 == null) {
                throw IncorrectInputException("ManufactureCost must be Double")
            }
            NotNullValidator<Double?>("ManufactureCost can't be null").checkValidness(manufactureCost1)
            this.manufactureCost = manufactureCost1!!
        }

        fun setUnitOfMeasure(unitOfMeasure: String?) {
            val unitOfMeasure1: UnitOfMeasure?
            if (unitOfMeasure != null && !UnitOfMeasure.containsOf(unitOfMeasure)) {
                throw IncorrectInputException("Such UnitOfMeasure doesn't exist (SQUARE_METERS, MILLILITERS, MILLIGRAMS)")
            }
            unitOfMeasure1 = unitOfMeasure?.let { UnitOfMeasure.valueOf(it) }
            NullableValidator<UnitOfMeasure?>().checkValidness(unitOfMeasure1)
            this.unitOfMeasure = unitOfMeasure1
        }

        fun setManufacturer(manufacturer: Organization?) {
            NullableValidator<Organization?>().checkValidness(manufacturer)
            this.manufacturer = manufacturer
        }

        fun setId(id: String?, ids: Set<Long>) {
            val id1: Long? = id?.toLongOrNull()
            if (id1 == null && id != null) {
                throw IncorrectInputException("Id must be Long")
            }
            NotNullValidator(
                MoreThanZeroValidator(
                    UniqueValidator(
                        "This id already exist. They can't be repeated.",
                        ids
                    ), "Id must be more than 0"
                ), "id can't be null"
            ).checkValidness(id1)
            this.id = id1
        }

        fun setCreationDate(creationDate: String?) {
            var creationDate1: LocalDateTime? = null
            if (creationDate != null) {
                creationDate1 = LocalDateTime.parse(creationDate)
            }
            NotNullValidator<LocalDateTime>("CreationDate can't be null").checkValidness(creationDate1)
            this.creationDate = creationDate1

        }

        fun build(): Product {
            if (creationDate == null) {
                creationDate = LocalDateTime.now()
            }
            if (id == null) {
                id = abs(Objects.hash(name, coordinates, price, partNumber, manufactureCost, unitOfMeasure, manufacturer).toLong())
            }
        return Product(name, coordinates, price, partNumber, manufactureCost, unitOfMeasure, manufacturer, creationDate!!, id!!)
        }
    }

    override fun compareTo(other: Product): Int {
        if (price == null && other.price == null) {
            return 0
        } else if (price == null) {
            return -1
        } else if (other.price == null) {
            return 1
        } else {
            return price.compareTo(other.price)
        }
    }
}