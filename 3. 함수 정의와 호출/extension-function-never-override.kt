fun View.showOff() {
    println("I'm a view!")
}
fun Button.showOff() {
    println("I'm a button!")
}
fun main() {
    val view: View = Button() // 실제 런타임에는 Button 객체이겠지만,
    view.showOff() // 확장 함수는 정적 바인딩이므로 View.showOff()가 호출된다.
}