fun modify(value: Any) {
    if (value is String) { // 타입 검사, 컴파일러는 이 분기에서 value가 String임을 알게 된다.
        println(value.uppercase()) // 따라서 따로 변환하지 않고 String 타입 메서드를 사용할 수 있다.
    }
}