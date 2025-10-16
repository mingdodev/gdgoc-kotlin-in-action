# 14장. 코루틴

### 목차

- [14.1 동시성과 병렬성](#141-동시성과-병렬성)
- [14.2 스레드와 코루틴의 차이](#142-스레드와-코루틴의-차이)
- [14.3 일시 중단 함수](#143-일시-중단-함수)
- [14.4 코루틴 vs. 동시성을 만드는 다른 접근 방법](#144-코루틴-vs-동시성을-만드는-다른-접근-방법)
    - [14.4.1 콜백](#1441-콜백)
    - [14.4.2 퓨처(Future)](#1442-퓨처future)
    - [14.4.3 반응형 스트림(RxJava)](#1443-반응형-스트림rxjava)
    
- [14.5 코루틴 빌더](#145-코루틴-빌더)
- [14.6 코루틴 디스패처](#146-코루틴-디스패처)
- [14.7 코루틴 컨텍스트](#147-코루틴-컨텍스트)

<br>

---

## 14.1 동시성과 병렬성

- 동시성

    - 여러 작업을 동시에 실행하는 것
    - 물리적으로 동시에 실행하지 않고, 동시성 태스크를 전환해 실행하는 것도 동시성을 달성한다.

- 병렬성

    - 여러 작업을 여러 CPU 코어에서 물리적으로 동시에 실행하는 것

코루틴을 사용하면 동시성 계산과 병렬성 계산을 모두 할 수 있다.

<br>

---

## 14.2 스레드와 코루틴의 차이

### 스레드

- 독립적으로 동시에 실행되는 코드 블록을 지정할 수 있다.

```import kotlin.concurrent.thread

fun main() {
    println("I'm on ${Thread.currentThread().name}")
    thread {
        println("And I'm on ${Thread.currentThread().name}")
    }
}
// I'm on main
// And I'm on Thread-0
```

- JVM에서 생성하는 각 스레드는 운영체제가 관리하는 스레드 (시스템 스레드)
- 메모리 할당과 컨텍스트 스위칭으로 인해 비용이 많이 든다. 효과적으로 관리할 수 있는 건 한 번에 몇 천 개의 스레드뿐
- 또한 스레드가 어떤 작업이 완료되길 기다리는 동안에는 블록된다.
- 독립적인 프로세스이므로 작업을 관리하고 조정하기 어렵다. '계층'의 개념이 없다.

<br>

### 코루틴

- **일시 중단 가능한 계산**을 나타낸다.
- 스레드의 초경량 추상화로, 일반 노트북에서도 10만 개 이상의 코루틴 쉽게 실행 가능
- 시스템 자원을 블록시키지 않고 실행을 일시 중단할 수 있으며, 나중에 중단된 지점에서 실행을 재개할 수 있다. 비동기 작업에서 블로킹 스레드보다 효율적이다.
- **구조화된 동시성**을 통해 동시 작업의 구조와 계층을 확립한다. 이를 통해 취소 및 오류 처리를 위한 메커니즘을 활용할 수 있다. 예를 들어 동시 계산의 일부가 실패했을 때, 자식으로 시작된 다른 코루틴들도 함께 취소되도록 보장한다.
- 하나 이상의 JVM 스레드에서 실행된다.

<br>

### cf. 프로젝트 룸(Project Loom)

- 자바 21에서는 **가상 스레드**라는 경량 동시성을 도입해 JVM 스레드와 운영체제 스레드 간의 일대일 결합을 해소하려 했다.
- 프로젝트 룸의 주요 목표는 기존의 I/O 중심 레거시 코드를 가상 스레드로 포팅할 수 있게 하는 것이다. 이는 큰 장점이지만, 반면 호환성을 위해 레거시 코드의 변경을 최소화하다보니 빠른 로컬 계산(CPU 작업)을 하는 함수인지 느린 네트워크 I/O 함수인지 언어 상에서 구별을 할 수가 없다.
- 반면 코루틴은 `suspend` 키워드를 통해 성격이 다른 두 작업을 코드 상에서 읽고 구별할 수 있다.
- [가상 스레드 관련 아티클](https://techblog.woowahan.com/15398/)

<br>

---

## 14.3 일시 중단 함수

> 잠시 멈출 수 있는 함수. 스레드 블로킹으로 인한 문제를 해결한다.

- 코루틴이 다른 동시성 접근 방식에 비해 지니는 장점은 대부분의 경우 **코드 형태를 크게 변경할 필요가 없다**는 점이다. 코드는 여전히 **순차적**으로 보인다.

<br>

```kotlin
suspend fun login(credentials: Credentials): UserId
suspend fun loadUserData(userId: UserId): UserData
fun showData(data: UserData)

suspend fun showUserInfo(credentials: Credentilas) {
    val userId = login(credentials)
    val userData = loadUserData(userId)
    showData(userData)
}
```
- 함수에 `suspend` 변경자를 붙이면, 함수가 다른 작업을 기다리는 동안 실행이 일시 중단 될 수 있다는 것을 의미한다. **일시 중단은 기저 스레드를 블록시키지 않는다**. 대신 다른 코드가 같은 스레드에서 실행될 수 있다.
- 이러한 코드 흐름 제어가, **코드 구조를 변경하지 않고** 수행된다. 또한 코드는 여전히 위에서 아래로 명령문 하나하나가 순차적으로 실행된다.
- 코루틴으로 실행되는 함수도 이 흐름을 고려해 작성해야 한다.
    - 예를 들어 네트워크 요청의 경우 케이토 HTTP 클라이언트, 레트로핏, OkHttpj

<br>

---

## 14.4 코루틴 vs. 동시성을 만드는 다른 접근 방법

> 너무 너무 궁금했어요 너무 신나

### 14.4.1 콜백

```kotlin
fun loginAsync(credentilas: Credentials, callback: (UserId) -> Unit)
fun loadUserDataAsync(userId: UserId, callback: (UserData) -> Unit)
fun showData(data: UserData)

fun showUserInfo(credentials: Credentials) {
    loginAsync(credentials) { userId ->
        loadUserDataAsync(userId) { userData ->
            showData(userData
            )
        }
    }
}
```
- 복잡한 순차적 의존성을 처리해야 할 경우, 콜백을 사용하면 뎁스가 깊어진다. **콜백 지옥!!**

> 요즘은 이를 해결하기 위한 새로운 기술들이 많이 나와서 콜백 지옥이 발생할 일은 거의 없지 않나? 했는데, 아무래도 새로운 기술을 배우는 데 드는 리소스가 있긴 하다. 콜백의 개념 자체는 이벤트 기반 비동기 처리를 할 때 최적의 솔루션이기도 하고! 새로운 기술들도 내부 시스템에서는 콜백으로 처리한다. 콜백 지옥이 왜 발생하는지 이해하고 있다면, 지옥같은 코드를 짤 일은 없을 것이야~

<br>

### 14.4.2 퓨처(Future)


```kotlin
fun loginAsync(credentilas: Credentials): CompletableFuture<UserId>
fun loadUserDataAsync(userId: UserId): CompletableFuture<UserData>
fun showData(data: UserData)

fun showUserInfo(credentials: Credentials) {
    loginAsync(credentials)
        .thenCompose { loadUserDataAsync(it) }
        .thenAccept { showData(it) }
}
```
- 퓨처는 **비동기 작업의 실행과 결과를 분리**한다.
- `loginAsync()`를 호출하는 순간 작업은 즉시 백그라운드 스레드에서 실행되도록 예약된다. 함수는 결과가 아니라 미래의 결과를 담을 수 있는 컨테이너인 **Future 객체를 즉시 반환**한다. 따라서 기저 스레드가 블로킹되지 않는다.
- `thenCompose`가 콜백을 예약하여, 작업이 완료되면 다음 작업을 실행하도록 한다.
- 콜백 지옥은 해결하지만 함수의 반환 타입을 `CompletableFuture`로 감싸야 하며, 새로운 연산자의 의미를 알아야 한다.

> 선언형 프로그래밍은 try-catch로 감싸는 대신 별도의 예외 처리 체인을 추가해야 한다는 점이 조금 복잡. 

<br>

### 14.4.3 반응형 스트림(RxJava)

```kotlin
fun login(credentilas: Credentials): Single<UserId>
fun loadUserData(userId: UserId): Single<UserData>
fun showData(data: UserData)

fun showUserInfo(credentials: Credentials) {
    login(credentials)
        .flatMap { loadUserData(it) }
        .doOnSuccess { showData(it) }
        .subscribe()
}
```
- 이 역시도 인지적 부가 비용이 있고, 새로운 연산자를 사용해야 한다. 반면, 코루틴은 `suspend`만 추가하여 스레드 블로킹을 피할 수 있다.

> 얘도 선언형 프로그래밍이라 기존의 동기 코드처럼 작성할 수 없음. 사실 프로그램 전체가 Reactive하면 선언형 코드의 장점이 극대화될 것 같은데, 동기 프로그램의 일부만 비동기 처리하려고 하면 굉장히 불편했음. 반환 값을 하나 하나 Mono로 감싸고.. 흐름 중 하나라도 블로킹되면 효율이 떨어지고.. 동기로 처리해야 하는 부분을 관리하기 어려웠다. 새로운 기술을 익숙하지 않은 채로 쓰니 NPE가 발생하기도 했다(!!) 선언형 코드는 어디서 예외가 발생할지 예측하기 더 어렵더라..

<br>

- 코틀린에도 `deferred`라는 자체적인 퓨처 스타일이 있고, 코루틴용 반응형 스트림 스타일의 추상화인 플로우라는 개념도 존재한다. 기존 코드가 다른 동시성 모델을 사용하고 있다면 코틀린은 그런 요소를 코루틴 친화적인 버전으로 변환할 수 있는 확장 함수를 제공한다.

<br>

---

## 14.5 코루틴 빌더

### 들어가며: 일시 중단 함수의 호출

- 일시 중단 함수는 **실행을 잠깐 중단할 수 있는 코드 블록** 안에서만 호출할 수 있다. 함수가 실행을 일시 중단할 수 있다면 그 함수를 호출하는 함수의 실행도 잠재적으로 일시 중단될 수 있음을 기억해야 한다.
- 그러려면 맨 처음 일시 중단 함수를 호출할 때 `suspend` 키워드를 사용해야 하는데, main 함수의 시그니처를 변경하는 게 과연 항상 옳을까?
- **코루틴 빌더**는 새로운 코루틴을 생성하는 역할을 하며, 일시 중단 함수를 호출하기 위한 일반적인 진입점으로 사용된다.

<br>

- `runBlocking`은 블로킹 코드와 일시 중단 함수의 세계를 연결할 때 쓰인다.
- `launch`는 값을 반환하지 않는 새로운 코루틴을 시작할 때 쓰인다.
`async`는 비동기적으로 값을 계산할 때 쓰인다.

<br>

### 14.5.1 runBlocking 함수

- 메인 스레드나 테스트 코드처럼 **동기적인 환경**에서 **비동기적인 코루틴이 완료될 때까지 기다리도록** 설계된 코루틴 빌더
- 동기 코드들이 작업이 완료될 때까지 먼저 실행되는 것을 막는다.
- 이때, 내부의 코루틴들은 비블로킹 방식(`launch`, `async`)으로 스레드를 해방하며 동시성을 극대화한다.
    - I/O 작업을 만나 `suspend`되면 자신을 실행하던 스레드를 즉시 해방한다. **I/O 작업을 기다리는 건 OS 커널에 위임**되고, 작업이 완료되면 커널이 JVM에게 이를 알려준다. Dispatcher는 유휴 스레드를 사용하여 다음 흐름을 계속 실행한다.

> cf. Reactive도 OS 커널의 I/O 위임 능력을 활용해 논블로킹을 구현하는 것.

```kotlin
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.miliseconds

suspend fun doSomthingSlowly() {
    delay(500.miliseconds)
    println("I'm done")
}

fun main() = runBlocking {
    doSomthingSlowly()
}
```
- runBlocking은 하나의 스레드를 블로킹한다. 대신 이 코루틴 안에서는 추가적인 자식 스레드를 얼마든지 시작할 수 있고, 이들은 다른 스레드를 더 이상 블록시키지 않는다. 대신, 일시 중단될 때마다 하나의 스레드가 해방되어 다른 코루틴이 코드를 실행할 수 있게 된다.

<br>

### 14.5.2 launch 함수

- 새로운 자식 코루틴을 시작할 때 쓰는 빌더
- **어떤 코드를 실행하되 그 결괏값을 기다리지 않는 경우**에 적합

```kotlin
private var zeroTime = Systme.currentTimeMills()
fun lolg(message: Any?) =
    println("${System.currentTimeMillis() - zeroTime} " +
    "[${Thread.currentThread() - name}] $message")

fun main() = runBlocking {
    log ("The first, parent, coroutine starts") // 1
    launch {
        log("The second coroutine starts and is ready to be suspended") // 3
        delay (100 milliseconds)
        log("The second coroutine is resumed") // 5
    }
    launch {
        log ("The third coroutine can run in the meantime") // 4
    }
    log("The first coroutine has launched two more coroutines") // 2
}
```
- 로깅, 파일/데이터베이스 쓰기처럼 부수 효과를 일으키지 않는 작업에 적합하다.
- launch는 Job 타입의 객체를 반환하는데, 이는 시작된 코루틴에 대한 핸들이다. 코루틴 실행을 제어(취소 등)할 수 있다.

<br>

### 14.5.3 async 함수

- 비동기 계산을 수행할 때 계산 결과를 반환하는 경우 사용하는 빌더. 대기 가능한 연산.
- 반환 타입은 `Deffered<T>` 인스턴스다. `await`이라는 일시 중단 함수로 그 결과를 기다리는 역할을 한다.

```kotlin
suspend fun slowlyAddNumbers(a: Int, b: Int): Int {
    log("Waiting a bit before calculating $a + $b")
    delay(100.milliseconds * a)
    return a + b
}

fun main() = runBlocking {
    log("Starting the async computation") // 1
    val myFirstDeferred = async { slowlyAddNumbers (2, 2) }
    val mySecondDeferred = async { slowlyAddNumbers (4, 4) }
    log("Waiting for the deferred value to be available") // 2
    // suspend fun이 순차적으로 실행됨
    10g("The first result: $(myFirstDeferred await()}") // 3
    1og("The second result: ${mySecondDeferred.await()}") // 4
}
```
- `async`를 호출할 때마다 새로운 코루틴을 시작함으로써 두 계산이 동시에 일어나게 했다.
- `await`를 호출하면 그 **Deffered(아직 사용할 수 없는 값을 나타냄)**에서 결괏값이 사용 가능해질 때까지 루트 코루틴이 일시 중단된다.
- 전체 계산 시간은 가장 늦게 끝난 작업이 수행되는 데 걸린 시간이 된다.
- 다른 언어와 다르게 기본적인 코드에서 일시 중단 함수를 순차적으로 호출할 때에는 `async, awit`를 사용할 필요가 없다. 결과를 기다려야 할 때 사용.

<br>

---

## 14.6 코루틴 디스패처

> 코드가 실제로 어떤 스레드에서 실행될까?

- 디스패처는 코루틴을 실행할 스레드를 결정한다.
- 본질적으로 코루틴은 특정 스레드에 고정되지 않는다.
- 디스패처를 선택하여 코루틴을 특정 스레드로 제한하거나 스레드 풀에 분산시킬 수 있다.
- 일반적으로 부모 디스패처를 상속 받지만 용도에 맞게 지정할 수 있다.

<br>

### Dispatcher.Default

- 가장 일반적인 디스패처이다.
- CPU 코어 수만큼의 스레드로 구성된 스레드 풀을 기본으로 한다.

<br>

### Dispatcher.Main

- 사용자 인터페이스 요소를 다시 그리는 등의 작업을 할 때, UI 스레드나 메인 스레드에서 실행해야 할 때가 있다.
- 이런 작업을 안전하게 실행하기 위해 Main을 사용할 수 있따.

<br>

### Dispatcher.IO

- 블로킹되는 IO 작업을 처리할 때, 자동으로 확장되는 스레드 풀에서 실행된다.
- 예를 들어 데이터이스 시스템과 상호작용하는 서드파티 블로킹 API를 사용할 때를 생각. 외부 API는 코루틴을 염두에 둔 호출이 아니기 때문에 기본 디스패처로 호출하면 스레드 풀이 빨리 소진될 수 있다.
- CPU 집약적이지 않은 작업에 적합하다. 

<br>

```kotlin
launch(Dispathcers.Default) {
    // ...
}
```
- 디스패처를 지정해 코루틴을 시작하기

```kotlin
launch(Dispathcers.Default) {
    // ...
    withContext(Dispathcer.Main) {
        updateUI(result)
    }
}
```
- 코루틴 내에서 디스패처 바꾸기

<br>

### 코루틴, 디스패처에서의 스레드 안전성

- 코루틴은 동시성 문제를 알아서 해결해주지 않는다.
- 여러 코루틴이 동일한 데이터에 접근하는 경우 스레드 안전성을 고려해야 한다.

```kotlin
fun main() {
    runBlocking {
        var x = 0
        repeat(10_000) {
            launch(Dispatchers.Default) {
                x++
            }
        }
        delay(1.seconds)
        println(x)
    }
}
// Kotlin Playground: 9999
```
- 다중 스레드 디스패처에서 여러 코루틴이 실행되어 일부 작업이 서로의 결과를 덮어쓰게 된다.
- 코루틴은 이를 방지하기 위해 **Mutex** 잠금을 제공한다. **코드 임계 영역**이 한 번에 하나의 코루틴만 실행되게 보장할 수 있다.
- 스레드 안전한 데이터 구조에 대해서도 공부해보자.

<br>

```kotlin
fun main() {
    runBlocking {
         val mutex = Mutex()
        var x = 0
        repeat(10_000) {
            launch(Dispatchers.Default) {
                mutex.withLock {
                    x++
                }
            }
        }
        delay(1.seconds)
        println(x)
    }
}
// Kotlin Playground: 10000
```

<br>

---

## 14.7 코루틴 컨텍스트

- `CoroutineContext`는 디스패처, 코루틴의 생명 주기, 각종 메타데이터 등 코루틴의 문맥 정보를 포함하고 있다.
- 이 속성에 접근하면 필요한 코루틴 콘텍스트를 확인할 수 있다.
- 실제 구현은 코틀린 컴파일러가 처리한다.
- 코루틴 빌더나 withContext 함수에 인자를 전달하면 자식 코루틴의 콘텍스트에서 해당 요소를 덮어쓴다.

```kotlin
fun main() {
    runBlocking(Dispatchers.IO + CoroutineName("Coolroutine")) {
        introspect()
    }
}
```