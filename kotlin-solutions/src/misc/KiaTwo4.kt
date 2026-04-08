package misc

interface Focusable {
    fun setFocus(b: Boolean) = println("I ${if (b) "got" else "lost"} focus.")
    fun showOff() = println("I'm focusable!")
}

interface Clickable {
    fun click()
    fun showOff() = println("I'm the Clickable interface impl")
}

class Button : Clickable, Focusable {
    override fun click() = println(" I was clicked ")
    override fun showOff() {
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }

}

fun main() {
    val button = Button()
    button.click()
    button.showOff()
}