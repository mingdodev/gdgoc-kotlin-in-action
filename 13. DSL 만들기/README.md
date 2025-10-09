# 13장. DSL 만들기

코틀린 언어 특성을 이용해서 **도메인 특화 언어(DSL, Domain-Specific Language)**를 만들고 사용해 보자. 특히 **수신 객체 지정 람다, invoke 관례**를 자세히 살펴보며 코틀린다운 API 설계에 대해 알아보자.

목표는 **가독성, 유지보수성**을 가장 좋게 유지하는 것이다.

<br>

---

## 13.1 DSL의 정의와 필요성

### API란

- 소프트웨어 간의 상호 작용을 위한 통신 수단을 제공하는 것으로, 주로 **명령적**이며 '어떻게 동작하는가'에 집중한다.
- 표준 라이브러리, REST API, 운영체제의 시스템 호출 등

**[깔끔한 API란?]**

- 코드를 읽는 사람이 어떤 일이 벌어질지 명확히 이해한다.
- 불필요한 구문, 번잡한 준비 코드를 최소화한다.


### DSL이란

- 특정 종류의 문제나 작업을 표현하기 위해 설계된 언어로, 주로 **선언적**이며 '무엇을 할 것인가'에 집중한다.
- SQL (데이터베이스 질의에 특화), 정규식(문자열 조작에 특화), HTML/CSS (문서 구조 및 스타일 지정에 특화), Gradle/Maven Groovy DSL (빌드 과정 정의에 특화) 등

DSL은 간결하며, 선언적이므로 내부적으로 최적화하기 효율적이라는 장점이 있다. 그러나 범용 언어로 만든 호스트 애플리케이션과 DSL을 직접적으로 통합하기 힘들다는 단점이 있다. 둘의 상호작용을 컴파일 시점에 제대로 검증, 디버깅하기 어렵고 DSL을 위한 IDE 기능 제공도 어렵다.

따라서 코틀린은 내부 DSL을 만들 수 있게 해준다.

<br>

---

### 내부 DSL

- 외부 DSL과 반대로 내부 DSL은 범용 언어로 작성된 프로그램의 일부이다.
    - 동일한 문법이고 같은 언어이지만, 주 언어를 별도의 문법으로 사용하는 것을 말한다.

```SQL
select Country.name, count(Customer.id)
      from Country
inner join Customer
      on Country.id = Customer.country_id
    group by Country.name
    order by count(Customer.id) desc
    limit 1
```
- **외부 DSL(SQL)**의 경우 문자열 리터럴에 넣어 사용하며, IDE를 통해 코드 작성과 검증에 도움을 받는다.

```kotlin
(Country innerJoin Customer)
    .slice(Country.name, Count(Customer.id))
    .selectAll()
    .groupBy(Country.name)
    .orderBy(Count(Customer.id), order = SortOrder.DESC)
    .limit(1)
```
- **내부 DSL(Exposed 제공 DSL)**은 내부적으로 SQL과 동일한 프로그램이 생성되고 실행되지만, 일반 코틀린 코드로 작성하고 리턴 값을 코틀린 객체로 받을 수 있다.

### DSL의 구조

- API와 다른 DSL에만 존재하는 특징이 있다. **구조** 또는 **문법**이다.
    - 일반적으로 API의 호출과 다른 호출 사이에는 맥락이 유지되지 않는다. **명령-질의(Command-Query) API**라고 부른다.
- DSL의 메서드 호출은 **문법에 의해 정해지는 더 커다란 구조**에 속한다.

    - 예를 들어 코틀린 DSL SQL 질의를 실행하려면 필요한 결과 집합의 여러 측면을 기술하는 **메서드 호출을 조합**해야 했었다. 이는 질의에 필요한 인자를 메서드 호출 하나에 다 넘기는 것보다 읽기 쉽다.

    - 테스트 프레임 워크의 단언문도 가독성 좋게 작성할 수 있다.

    ```kotlin
    str should startWith("kot")

    // assertTrue(str.startsWith("kot")): JUnit
    // 코테스트는 메서드 호출을 연쇄시켜 구조를 만든다.
    ```

    - 또한 같은 맥락을 재사용할 수 있다. 예를 들어 람다 내포를 통해 구조를 만들어 코드 중복을 없앤다.

    ```kotlin
    dependencies { // 람다 내포. 중복 제거, 선언적
        implementation("org.jetbrains.exposed:exposed-core:0.40.1")
        implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
    }
    ```

<br>

---

## 13.2 코틀린의 특성을 활용해 DSL 만들기

### 수신 객체 지정 람다로 DSL 만들기

```kotlin
fun buildString(
    builderAction: (StringBuilder) -> Unit
): String {
    val sb = StringBuilder()
    builderAction(sb)
    retrun sb.toString()
}

fun main() {
    val s = buildString {
        it.append("Hello, ")
        it.append("World!")
    }
    println(s)
    // Hello, World!
}
```
- 위 코드는 함수의 파라미터로 함수 타입을 받는다. 따라서 람다 본문에서 매번 `it`을 사용해서 StringB`u`ilder 인스턴스를 참조해야 한다.

> 복습: 코틀린은 람다에 매개변수가 하나만 있을 때, 그 매개변수 이름을 `it`으로 자동 지정한다.

<br>

아래는 **수신 객체 지정 람다**를 활용한 코드이다. 우선 수신 객체 지정 람다가 어떤 효율을 제공하는지 복습하자.

```kotlin
fun buildString(
    builderAction: StringBuilder.() -> Unit
): String {
    val sb = StringBuilder()
    sb.builderAction() // StringBuilder 인스턴스를 람다의 수신객체로 넘긴다
    retrun sb.toString()
}

fun main() {
    val s = buildString {
        this.append("Hello, ")
        append("World!")
    }
    println(s)
    // Hello, World!
}
```
- 위 코드 함수를 정의할 때 **수신 객체가 지정된 함수 타입의 파라미터**를 선언한다. 따라서 `StringBuilder` 인스턴스를 람다의 수신 객체로 넘길 수 있다.
- 이에 따라 `this` 키워드로 (생략 가능) 수신 객체를 표현할 수 있게 된다.
- 즉 `builderAction`은 `StringBuilder` 클래스 안에 정의된 메서드가 아니며, sb는 확장 함수 호출할 때와 동일한 구문으로 호출할 수 있는 함수 타입의 인자일 뿐이다.

> `this`는 자바와 마찬가지로 현재 객체를 표현하거나, 확장 함수에서 수신 객체를 표현할 때 쓴다. 그러나 모호성이 해결해야 할 때만 사용하고 일반적으로는 생략.

```kotlin
String.(Int, Int) -> Unit
// . 왼쪽: 수신 객체 타입
// 괄호 내부: 파라미터 타입
// 화살표 오른쪽: 반환 타입
```
- 확장 함수의 타입 정의는 위와 같이 이루어진다.

<br>

실제 표준 라이브러리의 구현은 `apply` 함수를 사용하여 더 간결하다.

```kotlin
fun buildString(builderAction: StringBuilder.() -> Unit): String =
    StringBuilder().apply(builderAction).toString()
```
- `apply`는 인자로 받은 람다나 함수를 호출할 때, **자신의 수신 객체**를 **인자로 받은 람다나 함수의 암시적 수신 객체로 사용**한다.
- **cf.** `apply`도 제공받은 수신 객체로 확장 함수 타입의 람다를 호출한다는 건 같은데, 얘는 수신 객체를 첫 번째 인자로 받고, 수신 객체가 아닌 람다 호출 결과를 반환한다. 결과를 받아서 쓸 필요가 있냐 없냐에 따라 바꿔 쓸 수 있음.

> 아~~ 이제 좀 이해했다  
> 수신 객체 지정 람다가 젤 낯설다 엉엉 이건 많이 써봐야 잘 쓸 수 있겠다

<br>

#### HTML 빌더(코틀린 DSL) 안에서 수신 객체 지정 람다 사용

- HTML 빌더: HTML을 만들기 위한 코틀린 DSL. 타입 안전성을 보장한다.

> cf. 빌더는 객체 계층 구조를 선언적으로 정의할 수 있다. UI 컴포넌트 레이아웃 정의할 때 유용.

```kotlin
fun createSimpleTable() = createHTML().
    table {
        tr {
            td { +"cell" }
        }
    }
```
- 각 함수는 고차함수로, 수신 객체 지정 람다를 인자로 받는다.
- table 함수에 넘겨진 람다에서는 tr 함수를 사용해 `<tr>` HTML 태그를 만든다. 그러나 이 람다 밖에서는 tr이라는 함수를 찾을 수 없다. 나머지 함수들도 각자의 상위 구조에서만 접근 가능. **(문법, 구조)** 이러한 API 설계로 인해 HTML 언어의 문법을 따르는 코드만 작성할 수 있게 된다. **(도메인 특화)**

```kotlin
open class Tag

// 대문자 -> 유틸리티 클래스. 메서드들은 해당 타입을 수신 객체로 받는 람다를 인자로 받는다.

class TABLE : Tag {
    fun tr(init: TR.() -> Unit)
}

class TR : Tag {
    fun td(init: TD.() -> Unit)
}

class TD : Tag
```
- 각 블록의 이름 결정 규칙은 각 람다의 수신 객체에 의해 결정된다.
- 내포된 람다에서 외부 람다의 수신 객체를 참조한다면 헷갈릴 수 있다. 이를 막기 위해 `@DsllMarker` 어노테이션이 제공된다.

<br>

- DSL을 정적 타입 언어와 함께 사용하면 **추상화**된 구조를 만들어 도메인 규칙을 지키도록 만들 수 있으며, 반복되는 코드 조각을 새 함수로 묶어 **재사용**할 수 있다.

### invoke 관례로 DSL 만들기

invoke 관례를 사용하면 어떤 커스텀 타입의 객체를 함수처럼 호출할 수 있다.

<br>

---

## 13.3 실용적인 DSL 구성 예제

### 테스팅

### 날짜 리터럴

### 데이터베이스 질의

612p