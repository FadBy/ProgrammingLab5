package Product

enum class UnitOfMeasure private constructor(val text: String){
    SQUARE_METERS("SQUARE_METERS"),
    MILLILITERS("MILLILITERS"),
    MILLIGRAMS("MILLIGRAMS");

    companion object {
        fun containsOf(other: String): Boolean {
            for (en in values()) {
                if (other == en.text) {
                    return true
                }
            }
            return false
        }
    }
}
