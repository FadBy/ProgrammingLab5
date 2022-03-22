import java.io.FileReader
import java.io.FileWriter
import java.io.Reader
import java.io.Writer

class Data(val path: String) {
    /**
     * Return fileReader for current path
     */
    fun getReader() : Reader {
        return FileReader(path)
    }

    /**
     * Return fileWriter for current path
     */
    fun getWriter() : Writer {
        return FileWriter(path)
    }

    companion object {
        /**
         * Returns fileReader from certain path
         *
         * @param path certain path
         */
        fun getReader(path: String): Reader {
            return FileReader(path)
        }
        /**
         * Returns fileWriter from certain path
         *
         * @param path certain path
         */
        fun getWriter(path: String): Writer {
            return FileWriter(path)
        }
    }
}