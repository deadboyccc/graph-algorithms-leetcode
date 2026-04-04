package misc.kia_part_one

fun main1() {
    println("12.345-6.A".split(".", "-"))
}

class User(val id: Int, val name: String, val address: String)

fun saveUser(user: User) {
    if (user.name.isEmpty()) {
        throw IllegalArgumentException(
            "Can't save user ${user.id}: empty Name"
        )
    }
    if (user.address.isEmpty()) {
        throw IllegalArgumentException(
            "Can't save user ${user.id}: empty Address"
        )
    }
// Save user to the database
}

fun saveUser2(user: User) {
    fun validate(
        user: User,
        value: String,
        fieldName: String
    ) {
        if (value.isEmpty()) {
            throw IllegalArgumentException(
                "Can't save user ${user.id}: empty $fieldName"
            )
        }
    }
    validate(user, user.name, "Name")
    validate(user, user.address, "Address")
// Save user to the database
}

fun main() {
    saveUser(User(1, "", ""))
}

