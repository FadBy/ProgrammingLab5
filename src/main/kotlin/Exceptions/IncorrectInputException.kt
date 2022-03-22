package Exceptions

class IncorrectInputException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
}