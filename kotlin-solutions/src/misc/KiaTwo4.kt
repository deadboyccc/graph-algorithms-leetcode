package misc

import java.io.Serializable

interface Focusable {
    fun setFocus(b: Boolean) = println("I ${if (b) "got" else "lost"} focus.")
    fun showOff() = println("I'm focusable!")
}

interface Clickable {
    fun click()
    fun showOff() = println("I'm the Clickable interface impl")
}

class ButtonO : Clickable, Focusable {
    override fun click() = println(" I was clicked ")
    override fun showOff() {
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }

}
// 4.1.3

interface State : Serializable
interface View {
    fun getCurrentState(): State
    fun restoreState(state: State) { /* ... */
    }
}

class Button : View {
    override fun getCurrentState(): State = ButtonState()
    override fun restoreState(state: State) { /*...*/
    }

    class ButtonState : State { /*...*/ }
}

class OuterClass {
    inner class InnerClass {
        fun getOuterReference(): OuterClass = this@OuterClass
    }
}

sealed interface Toggleable {
    fun toggle()
}

class LightSwitch : Toggleable {
    override fun toggle() = println("Lights!")
}

class Camera : Toggleable {
    override fun toggle() = println("Camera!")
}

fun testWhenSealed(toggleable: Toggleable) {
    when (toggleable) {
        is LightSwitch -> println("LightSwitch toggleable")
        is Camera -> println("Camera toggleable")
    }
}

open class User(val name: String, val isAdult: Boolean = false)

fun main() {
    val button = ButtonO()
    button.click()
    button.showOff()
    outerForLoop@ for (i in 1..10) {
        innerForLoop@ for (j in 1..10) {
            println("i: $i  | j: $j")
            if (j == 3) break@outerForLoop

        }
    }
    val camera = Camera()
    val lightSwitch = LightSwitch()
    testWhenSealed(lightSwitch)
    testWhenSealed(lightSwitch)


}