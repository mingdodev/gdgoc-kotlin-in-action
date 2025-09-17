# 2장. 코틀린 기초

## 2.1 기본 요소: 함수와 변수

### 2.1.1 프로그램 진입점

```kotlin
fun main() {
    println("Hello, world!")
}
```

- 최상위 main 함수에 인자가 없어도 된다.
    - 문자열 배열(`args: Array<String>`)이 전달되기도 한다.
    - 이 경우 배열의 각 원소는 애플리케이션에게 전달된 각각의 커맨드라인 인자에 대응한다.
- 어떤 경우든 main 함수는 아무 값도 반환하지 않는다.
- 콘솔 출력은 `println`
- 줄 끝에 세미콜론을 붙이지 않아도 된다.

### 2.1.2. 함수 선언

```kotlin
fun max(a: Int, b: Int): Int {
    return if (a > b) a else b
}
```

- 코틀린에서는 파라미터 이름이 먼저 오고 그 뒤에 파라미터의 타입을 지정한다.
- 반환 타입은 파라미터 목록 괄호 다음에 `:`으로 구분하여 작성한다.

> 코틀린은 if가 **결과를 만드는 식(expression)**이라는 점에 집중한다. 어쨌든 값을 반환하는 것이다.  
> 코틀린의 if 식은 자바 같은 다른 언어의 삼항 연산자와 비슷하게 사용한다.

#### cf. 코틀린에서의 문(statement)과 식(expression)의 구분

코틀린에서 if는 식이지 문이 아니다. **식은 값을 만들어내며 다른 식의 하위 요소로 계산에 참여**할 수 있는 반면, **문은 자신을 둘러싸고 있는 가장 안쪽 블록의 최상위 요소로 존재하며 아무런 값을 만들어내지 않는다**.

자바와 달리, **코틀린에서는 루프(for, while, do/while)를 제외한 대부분의 제어 구조가 식**이다. 제어 구조를 다른 식으로 엮어낼 수 있다는 특징은 여러 일반적인 패턴을 아주 간결하게 표현할 수 있다는 장점을 만든다.

### 2.1.3 식(expression) 본문을 사용해 함수를 더 간결하게 정의

```kotlin
// 식 본문 함수
fun max(a: Int, b: Int): Int = if (a > b) a else b

// 타입 추론: 컴파일러가 함수 본문 식을 분석해서 식의 결과 타입을 함수 반환 타입으로 정해준다
fun max(a: Int, b: Int): Int = if (a > b) a else b
```

- max 함수의 본문이 if 식 하나로만 이뤄져 있기 때문에, 이 식을 함수 본문 전체로 하고 중괄호를 없앤 후 return을 제거할 수 있다.
- 유일한 식은 등호(=) 뒤에 위치시킨다.
- 식 본문 함수는 반환 타입을 생략할 수 있다.

#### 식 본문 함수와 블록 본문 함수

- 블록 본문 함수: 본문이 중괄호로 둘러싸인 함수
- 식 본문 함수: 등호와 식으로 이뤄진 함수

코틀린에서는 식 본문 함수가 자주 쓰인다.

> 코틀린의 이런 설계는 간결함을 자랑하면서도 필요할 때 필요한 정보를 읽을 수 있는 유지보수성을 보여준다.
>
> 블록 본문 함수의 경우 반드시 반환 타입을 지정, return 문을 사용해 반환값을 명시해야 한다. 아주 긴 함수에 return문이 여럿 들어있는 경우, 반환 타입과 return을 명시하면 그 함수가 어떤 타입의 값을 어디서 반환하는지 더 쉽게 알아볼 수 있다.  
> 같은 맥락에서, 라이브러리를 작성할 때에도 반환 타입을 명시해야 한다. 또, 실수로 함수 시그니처가 바뀌면서 소비자들의 코드에 오류가 발생하는 것을 막을 수 있다.

### 2.1.4 데이터를 저장하기 위해 변수 선언

- 변수를 정의하며 타입 선언

```kotlin
val question: String = "우주"
val answer: Int = 42
```

- 타입을 지정하지 않고 초기화: 타입 추론

```kotlin
val question = "우주"
val answer = 42
```
> 타입 추론을 한다고 해서 컴파일러가 막 더 힘들어하고 성능이 떨어지는 것은 아니라고 한다.

- 변수를 선언하되 초기화하지 않음

```kotlin
val answer: Int // 타입 명시
answer = 42
```

### 2.1.5 읽기 전용 변수 val과 재대입 가능 변수 var

#### val

- 읽기 전용 참조를 선언
- val로 선언된 변수는 단 한 번만 대입될 수 있다.
- 일단 초기화하고 나면 다른 값을 대입할 수 없다. `자바의 final`

```kotlin
fun canPerformOperation(): Boolean {
    return true
}

fun main() {
    val result: String
    if (canPerformOperation()) {
        result = "Success"
    } else {
        result = "Can't perform operation"
    }
}
```

- 해당 변수가 정의된 블록을 실행할 때 정확히 한 번만 초기화되어야 한다. (컴파일러가 초기화 문장이 하나만 실행되는지를 확인한다)

<br>

```kotlin
fun main() {
    val languages = mutableListOf("Java")
    languages.add("Kotlin")
}
```

- 한 번 대입된 다음 그 값을 바꿀 수 없더라도, **그 참조가 가리키는 객체의 내부 값은 변경될 수 있다**.
- 대신 다른 객체로 재할당되는 것은 막을 수 있다.

<br>

#### var

- 재대입 가능한 참조를 선언한다.
- `자바의 일반 변수`

기본적으로 코틀린에서는 모든 변수를 val 키워드를 사용해 선언하는 방식을 지켜야 한다. 읽기 전용 참조와 변경 불가능한 객체를 부수 효과가 없는 함수와 조합해 사용하면, 함수형 프로그래밍의 이점을 살릴 수 있다.
필요할 때에만 변수를 var로 선언한다.

```kotlin
var answer = 42
answer = "no" // type mismatch compile error
```

- var 키워드는 변수의 값 변경을 허용하지만 변수의 타입은 고정된다.

### 2.1.6 문자열 템플릿

```kotlin
val input = readln()
val name = if (input.isNotBlank()) input else "Kotlin"
println("Hello, $name!")
println("Hi, ${name.length}-letter person!") // {} 구문을 사용해 변수의 프로퍼티 역시 문자열 템플릿에 사용 가능
```

- **cf.** `$`를 문자열에 넣으려면 `\$`로 이스케이프

#### cf. 한글을 문자열 템플릿에서 사용할 경우 주의

```kotlin
fun main() {
    val name = "Hi"
    println("$name님, 반가워요!") // Unresolved reference compile error
}
```
한글을 변수명 뒤에 바로 붙이면 컴파일러가 둘을 한번에 식별자로 인식하게 된다.

```kotlin
fun main() {
    val name = "Hi"
    println("${name}님, 반가워요!")
}
```
따라서 다음과 같이 변수 이름을 {}로 감싸서 사용할 것!

<br>

```kotlin
println("Hi, ${if (name.isBlank()) "someone" else name}!")
```
템플릿이 조건식을 둘러쌀 수도 있다.

<br>

---

## 2.2 행동과 데이터 캡슐화: 클래스와 프로퍼티

#### POJO Class 

```java
public class Person {
    private final String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

#### Kotlin Class

```kotlin
class Person(val name: String)
```

- 코틀린은 간결한 클래스 정의가 가능
    - 코드 없이 데이터만 저장하는 클래스
- 코틀린의 기본 가시성은 public, 생략 가능

### 2.2.1 프로퍼티

> 클래스와 데이터를 연관시키고 접근 가능하게 만든다

```kotlin
class Person {
    val name: String, // 읽기 전용, 비공개 필드와 공개 getter 생성
    var isStudent: Boolean // 변경 가능, 비공개 필드와 공개 getter, 공개 setter 생성
}

- 코틀린은 자바의 비공개 필드 및 getter, setter로 이루어진 **프로퍼티**를 기본 기능으로 제공한다.
- 코틀린의 네이밍: is로 시작하는 프로퍼티의 getter는 get이 붙지 않고 원래 이름을 사용, setter는 is를 set으로 바꾼 이름을 사용한다.
- new 키워드를 사용하지 않고 생성자를 호출한다.
- 프로퍼티 이름을 직접 사용해도 자동으로 getter/setter를 호출한다.

> 코틀린의 네이밍 방식이 행위를 분명하게 나타내는가? 써봐야 알 것 같다.

### 2.2.2 커스텀 접근자(getter) 📌

> 프로퍼티 값을 저장하지 않고 계산

```kotlin
class Rectangle(val height: Int, val width: Int) {
    val isSquare: Boolean
        get() { // 프로퍼티 게터 선언
            return height == width)
        }
}
```

직사각형 클래스에서 높이와 너비를 저장할 때, 둘이 같으면 true를 돌려주는 `isSquare` 프로퍼티를 필드에 저장하지 않고도 제공할 수 있다. 이를 on the go property라고 하며, 커스텀 getter를 사용해 제공할 수 있다.

> 커스텀 getter는, 자바에서처럼 파라미터가 없는 함수를 정의하는 것과 성능 차이는 없다.  
> 일반적으로 클래스의 특성을 기술하고 싶다면 프로퍼티로, 행동을 기술하고 싶다면 멤버 함수를 고르면 된다.

### 2.2.3 디렉터리와 패키지

> 코틀린의 소스코드 구조에 대해 알아보자

#### 패키지

- 패키지 문이 있는 파일에 들어있는 모든 선언은 해당 패키지에 들어간다.
- 코틀린은 클래스 임포트와 함수 임포트를 구분하지 않는다.
- 코틀린은 여러 클래스를 같은 파일에 넣을 수 있고, 이름도 자유롭다. 디렉터리와 소스코드 파일의 위치도 관계없다.
    - 자바에서는 디렉터리 구조에 의존하는 패키지 구조, 클래스명을 따라가는 파일명

<br>

- **하지만 대부분의 경우 자바처럼 패키지별로 디렉터리를 구성하는 편이 낫다!** (호환성도 무시 못함)
- 그렇지만 여러 클래스를 한 파일에 넣는 것을 주저하지 말자.

<br>

---

## 2.3 선택 표현과 처리: Enum과 when

### 2.3.1 이넘 클래스와 이넘 상수 정의

### 2.3.2 when으로 이넘 클래스 다루기

### 2.3.3 when식의 대상을 변수에 캡처

### 2.3.4 when의 분기 조건에 임의의 객체 사용

### 2.3.5 인자 없는 when 사용

### 2.3.6 스마트 캐스트

> 타입 검사와 타입 캐스트를 한번에

### 2.3.7 if를 when으로 변경하는 방법 (리팩터링)

### 2.3.8 if와 when의 분기에서 블록 사용

<br>

---

## 2.4 대상 이터레이션: while과 for 루프

### 2.4.1 while 루프

### 2.4.2 수에 대한 이터레이션: 범위와 순열

### 2.4.3 맵에 대한 이터레이션

### 2.4.4 in

> 컬렉션이나 범위의 원소를 검사

<br>

---

## 2.5 코틀린 예외 처리

