fun fizzBuzz(i: Int) = when {
    i % 15 == 0 -> "fizz buzz "
    i % 3 == 0 -> "fizz "
    i % 5 == 0 -> "buzz "
    else -> "$i "
}

fun main() {
    for (i in 1..100) {
        print(fizzBuzz(i))
    }
}