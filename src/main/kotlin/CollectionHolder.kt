import Exceptions.ElementNotFoundException
import Exceptions.IncorrectInputException
import Product.Coordinates
import Product.Organization
import Product.Product
import java.util.*
import kotlin.collections.HashSet
import java.time.LocalDateTime
import kotlin.NoSuchElementException


class CollectionHolder(elementSet: Set<Product>) : Iterable<Product> {
    constructor() : this(TreeSet())

    val partNumbers = HashSet<String?>()
    val ids = HashSet<Long?>()

    init {
        for (i in elementSet) {
            add(i)
        }
    }

    val size
        get() = treeCollection.size

    val creationDate = LocalDateTime.now()

    val type: String = "HashSet"

    private val treeCollection = TreeSet<Product>(elementSet)
    val collection: HashSet<Product>
        get() = treeCollection.toHashSet()

    /**
     * Adds certain product to holder
     *
     * @param product product to add
     */
    fun add(product: Product) {
        ids.add(product.id)
        partNumbers.add(product.partNumber)
        treeCollection.add(product)
    }

    /**
     * Add product to holder
     */
    fun add(): Product {
        val productBuilder = Product.Builder()
        val coordinatesBuilder = Coordinates.Builder()
        val organizationBuilder = Organization.Builder()
        val printer = Printer.instance!!
        var i = 1
        while (true) {
            try {
                when (i) {
                    1 -> productBuilder.setName(printer.makeInputRequest("Name (String)"))
                    2 -> coordinatesBuilder.setX(printer.makeInputRequest("Coordinates.x (Long)"))
                    3 -> {
                        coordinatesBuilder.setY(printer.makeInputRequest("Coordinates.y (Long)"))
                        productBuilder.setCoordinates(coordinatesBuilder.build())
                    }
                    4 -> productBuilder.setPrice(printer.makeInputRequest("Price (Double)"))
                    5 -> productBuilder.setPartNumber(
                        printer.makeInputRequest("PartNumber (String)"),
                        partNumbers
                    )
                    6 -> productBuilder.setManufactureCost(printer.makeInputRequest("ManufactureCost (Double)"))
                    7 -> productBuilder.setUnitOfMeasure(
                        printer.makeInputRequest(
                            "UnitOfMeasure(SQUARE_METERS, MILLILITERS, MILLIGRAMS)",
                        )
                    )
                    8 -> organizationBuilder.setName(printer.makeInputRequest("Product.Organization.name (String)"))
                    9 -> organizationBuilder.setFullName(printer.makeInputRequest("Product.Organization.fullName (String)"))
                    10 -> organizationBuilder.setAnnualTurnover(
                        printer.makeInputRequest(
                            "Product.Organization.annualTurnover (Long)",
                        )
                    )
                    11 -> {
                        organizationBuilder.setEmployeesCount(
                            printer.makeInputRequest(
                                "Product.Organization.EmployeesCount (Long)",
                            )
                        )
                        productBuilder.setManufacturer(organizationBuilder.build())
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
        val product = productBuilder.build()
        add(product)
        return product
    }

    /**
     * Removes all products from holder
     */
    fun clear() = treeCollection.clear()

    /**
     * Finds product by id
     *
     * @param id id
     */
    fun findById(id: Long): Product? {
        for (prod: Product in treeCollection) {
            if (prod.id == id) {
                return prod
            }
        }
        return null
    }

    /**
     * Removes certain product
     *
     * @param product product
     * @return is remove success
     */
    fun remove(product: Product): Boolean = treeCollection.remove(product)

    /**
     * Gets minimum product from the holder
     */
    fun getMin(): Product {
        try {
            return treeCollection.first()
        } catch (e: NoSuchElementException) {
            throw ElementNotFoundException("Collection is empty", e)
        }
    }

    /**
     * Removes product from holder by id
     *
     * @param id id
     * @retyrn is remove success
     */
    fun removeById(id: Long): Boolean = treeCollection.remove(findById(id))

    /**
     * Removes all product from collection
     *
     * @param collection collection
     */
    fun removeAll(collection: Collection<Product>): Boolean = treeCollection.removeAll(collection.toSet())

    /**
     * Returns all product lower than certain
     *
     * @param product certain product
     * @return products lower than certain
     */
    fun headSet(product: Product): SortedSet<Product> = treeCollection.headSet(product)

    /**
     * Returns sorted set of products from holder in ascending order
     *
     * @return sorted set
     */
    fun getAscendingOrder(): NavigableSet<Product> = treeCollection

    /**
     * Returns sorted set of product from holder in descending order
     *
     * @return sorted set
     */
    fun getDescendingOrder(): NavigableSet<Product> = treeCollection.descendingSet()

    /**
     * Return holder iterator
     */
    override fun iterator(): Iterator<Product> = treeCollection.iterator()
}
