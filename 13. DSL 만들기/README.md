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

### invoke 관례로 DSL 만들기

invoke 관례를 사용하면 어떤 커스텀 타입의 객체를 함수처럼 호출할 수 있다.

<br>

---

## 13.3 실용적인 DSL 구성 예제

### 테스팅

### 날짜 리터럴

### 데이터베이스 질의

612p