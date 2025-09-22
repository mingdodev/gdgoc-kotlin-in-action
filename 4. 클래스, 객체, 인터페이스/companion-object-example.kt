class Person(val name: String) {
    companion object Loader {
        fun fromJson(json: String): Person {
            // 간단한 예시로, 실제 JSON 파싱은 하지 않음
            val name = json.substringAfter(":\"").substringBefore("\"")
            return Person(name)
        }
    }

    fun main() {
        val person = Person.Loader.fromJson("""{"name":"do"}""")
        println(person.name)
        // do
    }
}