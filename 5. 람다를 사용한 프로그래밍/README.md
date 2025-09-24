# 5장. 람다를 사용한 프로그래밍

- 람다식, 멤버 참조를 사용해 코드 조각과 행동 방식을 함수에게 전달

# 5.1 람다식과 멤버 참조

## 람다: 코드 블록을 값으로 다루는 것

- "이벤트가 발생하면 이 핸들러를 실행하자", "데이터 구조의 모든 원소에 이 연산을 적용하자"와 같은 상황에서 람다는 유용하다.
- 람다는 메서드가 하나뿐인 익명 객체(함수형 인터페이스의 구현) 대신 사용할 수 있다.

### 자바 8 이전: 익명 (내부) 클래스를 통한 함수형 인터페이스 구현

```java
public static void main(String[] args) {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() /* ... */
    });
}
```
자바 8 이후에는 람다 표현식으로 다음과 같이 작성할 수 있다.

```java
Thread thread = new Thread(() -> {
    /* ... */
});
// 함수를 매개변수로 전달했다. 이 경우는 Thread 생성자 중 Runnable을 인자로 받는 생성자에 람다식으로 Runnable을 넘긴 것이다. 컴파일러의 타입 추론으로 Runnable 타입을 명시하지 않아도 된다. 만약 모호한 경우가 생기면 명시해야 할 것.
```
함수를 값처럼 다룬다. 람다식을 사용하면 함수를 선언할 필요가 없다.

## 람다와 컬렉션

```kotlin
fun main() {
    val people = listOf(Person("mingdo", 23), Person("doming", 22))
    println(people.maxByOrNull { it.age })
    // 함수의 인자가 람다일 때는 괄호 생략 가능. 함수 레퍼런스로도 쓸 수 있음. people.maxByOrNull(Person::age)
}
```
- 람다를 이용하면 컬렉션의 좋은 메서드들을 잘 활용할 수 있다.

> 함수 호출 시 맨 뒤 인자가 람다식이면 그 람다를 괄호 밖으로 빼낼 수 있다.
 람다가 어떤 함수의 유일한 인자이고 괄호 뒤에 람다를 썼다면 호출 시 빈 괄호를 없애도 된다.

```kotlin
{ x: Int, y: Int -> x + y }
```

- 코틀린 람다식은 항상 중괄호로 둘러싸여 있다.

```kotlin
fun main() {
    run { println(123) }
}
```
- 인자로 받은 람다를 실행해 주는 라이브러리 함수 `run`