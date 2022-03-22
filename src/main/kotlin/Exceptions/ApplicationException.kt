package Exceptions

class ApplicationException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
}