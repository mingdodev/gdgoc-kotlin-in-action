val x = if (myBoolean) 3 else 5
val direction = when (inputString) {
    "u" -> UP
    "d" -> DOWN
    else -> UNKNWON
}
val number = try {
    inputString.toInt()
} catch (nfe: NumberFormatException) {
    -1
}