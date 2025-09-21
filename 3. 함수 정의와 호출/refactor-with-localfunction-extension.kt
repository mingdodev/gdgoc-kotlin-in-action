class User(val id: Int, val name: String, val address: String)
// 만약 private로 만들고 싶다면 private val로 선언해야 함

fun User.validateBeforeSave() {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException("Can't save user $id: empty $fieldName")
        }
    }
    validate(name, "Name")
    validate(address, "Address")
}

fun saveUser(user: User) {
    user.validateBeforeSave()
}