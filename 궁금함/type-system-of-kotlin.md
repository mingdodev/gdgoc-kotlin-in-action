## 코틀린의 타입 시스템은 자바와 어떻게 다른가?

### 코틀린은 모든 것을 객체로 다룬다.

- `Int`, `Double`, `Char`, `Boolean` 등의 기본 타입
    - `toString()`, `hashCode()` 내장
    - 하지만 컴파일 후 JVM에서 실행될 때는 내부적으로 자바의 원시 타입을 사용해 성능을 최적화한다.
    > 그러면 뭔가 개발자가 볼 수 없는 부분에서 예기치 않은 동작을 하는 것이 아닌가?! 라고 생각했는데, 코틀린의 추상화는 오히려 예측 가능하고 안전한 동작을 보장하는 것이라고 한다.
    > 생각해보면 개발자가 int와 Integer 변환을 하는 코드를 작성하는 것보다 이미 안전하게 정의된 매커니즘을 가진 컴파일러가 낫겠지...

- `String`: 불변 객체

### 코틀린에서 최상위 클래스의 개념을 하는 것은 `Any`이다.

자바의 `Object`를 보면..

```java
package java.lang;

public class Object {

    /**
     * Constructs a new object.
     */
    @IntrinsicCandidate
    public Object() {}
    ...
    @IntrinsicCandidate
    public final native void notify();
    ...
    public final void wait() throws InterruptedException {
        wait(0L);
    }
    ...
```

> 기본적으로 메서드가 엄청 많다

- notify, wait 같은 동시성 메서드가 포함되어있다.

반면!!

```kotlin
package kotlin

/**
 * The root of the Kotlin class hierarchy. Every Kotlin class has [Any] as a superclass.
 */
public open class Any {

    public open operator fun equals(other: Any?): Boolean

    public open fun hashCode(): Int

    public open fun toString(): String
}
```

- 단 세 개의 메서드만 존재한다.

- 코틀린은 모든 객체가 불필요하게 스레드 관련 기능을 가지는 것을 방지하여, 코드를 더 간결하고 안전하게 만든다.
- 동시성 처리는 코루틴과 라이브러리를 통해 다룬다.