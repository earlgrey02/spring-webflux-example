## Spring WebFlux

> **Spring framework에서 반응형 및 비동기 애플리케이션 개발을 지원하는 모듈**

## Spring WebFlux의 특징

### 논블로킹(Non-blocking)

Spring WebFlux에서는 서블릿(Servlet) 컨테이너인 Tomcat을 사용하는 Spring Web MVC와 달리 이벤트 루프를 가진 [Netty](https://github.com/netty/netty)를
사용한다.
Spring Web MVC가 사용하는 Tomcat은 전통적인 블로킹 IO(`InputStream`, `OutputStream`)를 사용한다.
그러나 Spring WebFlux는 Netty의 이벤트 루프를 통해 논블로킹 IO를 사용할 수 있다.
이벤트 루프는 Java NIO의 `Selector`와 `ByteBuf`를 기반으로 한 멀티 플렉싱(Multiplexing) 기법을 사용하는데,
이를 통해 하나의 요청에 하나의 스레드가 배정되는 Tomcat과 달리 적은 수의 스레드로 많은 요청을 처리할 수 있다.

```java

@FunctionalInterface
public interface LoopResources extends Disposable {
    int DEFAULT_IO_WORKER_COUNT = Integer.parseInt(System.getProperty("reactor.netty.ioWorkerCount", "" + Math.max(Runtime.getRuntime().availableProcessors(), 4)));
    
    ...
}
```

위 코드에서 확인할 수 있듯이, Spring WebFlux는 기본적으로 이벤트 루프 스레드를 최소 4개 ~ CPU 코어 수만큼 가지고 있다.
이렇게 적은 수의 스레드만으로 요청을 처리하므로 이벤트 루프 스레드를 블로킹하는 코드가 있다면 성능에 큰 영향을 미친다.
그러므로 기존의 JDBC(Java Database Connectivity) 등의 블로킹 기술을 그대로 사용할 수 없다는 특징을 가지고 있다.

이러한 블로킹 기술들은 [R2DBC(Reactive Relational Database Connectivity)](https://github.com/r2dbc), Spring Data Reactive Mongo
등의 논블로킹을 지원하는 기술들로 바꿔서 사용할 수 있다.

### 리액티브 프로그래밍(Reactive Programming)

Spring WebFlux는 데이터 스트림과 변화에 반응하는 시스템을 구축하기 위한 프로그래밍 패러다임인 리액티브 프로그래밍을 사용한다.
리액티브 프로그래밍은 구독 및 발행 패턴(Pub / Sub 패턴)을 통한 비동기 메세지 통신을 사용한다.
이러한 리액티브 프로그래밍의 표준 사양으로 리액티브 스트림즈(Reactive Streams)가 있으며, 리액티브 스트림즈의 구현체로는 RxJava, Reactor 등이 있다.

Spring WebFlux는 여러 리액티브 스트림즈 구현체 중 Reactor를 사용한다.
그렇기에 Netty 또한 `ChannelFuture`가 아닌 Reactor의 `Publisher` 구현체인 `Mono`와 `Flux`를 처리할 수
있는 [Reactor Netty](https://github.com/reactor/reactor-netty)를 사용한다.

또한 리액티브 프로그래밍은 선언형 프로그래밍을 따른다.

## Spring Web MVC vs Spring WebFlux

Spring WebFlux는 Spring Web MVC보다 더 적은 자원으로 많은 요청을 처리할 수 있는 리액티브 애플리케이션을 만들기 위해 사용한다.
이렇게 보면 무조건 Spring WebFlux를 선택하는 것이 좋은 선택이라 생각될 수 있다.
그러나 Spring WebFlux는 Reactor의 높은 러닝커브, 기존의 JPA(Java Persistence API) 등 생산성 높은 기술의 부재 등의 문제를 가지고 있다.

특히 Spring WebFlux에서 관계형 데이터베이스를 다룰때 사용하는 R2DBC와 Spring Web MVC에서 사용하던 JPA 간의 생산성 차이가 굉장히 크다.
다행히 NoSQL 진영은 기존의 Spring Web MVC에서 사용하던 부분과 큰 차이가 없다.

개인적으로 필자는 Spring Web MVC와 Spring WebFlux는 팀의 기술 인력에 따라 결정된다고 생각하며,
만약 팀이 Spring WebFlux에 익숙하다면 비교적 성능이 낮은 Spring Web MVC를 굳이 선택할 이유는 없다고 생각한다.
예외적으로 CPU Bound가 많은 애플리케이션은 이벤트 루프 모델보다는 Thread per request 모델을 사용하는 Spring Web MVC가 좀 더 성능이 높다.

## Reactor vs Coroutine

Spring WebFlux 안에서도 또 다른 기술 선택지가 있다.
기존의 Reactor와 높은 러닝 커브를 가진 Reactor를 대체하는 Kotlin의 Coroutine이다.
개인적으로 둘 다 성능보다는 코드 스타일에 차별점을 두고 있어서 본인이 선호하는 코드 스타일(명령형 프로그래밍 또는 선언형 프로그래밍)에 따라 결정할 수 있는 부분이라 생각한다.
물론 Reactor는 배압(Back Pressure)을 지원한다는 차별점도 가지고 있긴 하다.

### Reactor

앞서 설명했듯이 Reactor는 리액티브 스트림즈 구현체로, 일반적으로 0~1개의 데이터를 발행하는 `Mono`와 N개의 데이터를 발행하는 `Flux`로 구성된다.

```kotlin
@Transactional
fun deletePostById(id: String, authentication: DefaultJwtAuthentication): Mono<Void> =
    postRepository.findByIdAndDeletedDateIsNull(id) // 게시글 조회
        .switchIfEmpty(Mono.error(PostNotFoundException()))
        .filter { it.writer.id == authentication.id } // 권한 확인
        .switchIfEmpty(Mono.error(PermissionDeniedException()))
        .flatMap {
            LocalDateTime.now()
                .let { now ->
                    postRepository.save(it.copy(deletedDate = now)) // 게시글 삭제 처리
                        .zipWith( // 동시성 처리
                            commentRepository.findAllByPostIdAndDeletedDateIsNull(id) // 게시글에 달린 댓글 전체 조회
                                .map { it.copy(deletedDate = now) }
                                .flatMap { commentRepository.save(it) } // 댓글 삭제 처리
                                .then(Mono.just(true))
                        )
                }
        }
        .then()
```

Reactor는 선언형 프로그래밍의 일종인 함수형 프로그래밍을 사용하는데, 부수 효과(Side Effect)가 없는 순수 함수를 다룬다는 장점이 있으나 메서드 체이닝이 길어질 수록 코드가
복잡해진다는 단점도 가지고 있다.
또한 Reactor의 많은 연산자들에 대한 이해가 필요해 러닝 커브가 높다.

### Coroutine

코루틴(Coroutine)은 경량형 스레드라 불리는 서브 루틴과 일시 중지 가능한 함수(Suspend function)를 사용하는 멀티 태스킹 기법으로, 특정 서브 루틴이 네트워크 IO 등으로 인해 일시 중지되면
이전에
중지되었던 다른 서브 루틴이 작업을 재개하는 방식으로 작동한다. 스레드에 배정된 서브 루틴들이 중지되는 것은 자기 자신이지 스레드가 아니므로 논블로킹이다.

```kotlin
@Transactional
suspend fun deletePostById(id: String, authentication: DefaultJwtAuthentication): Unit = coroutineScope {
    val now = LocalDateTime.now()
    val post = postRepository.findByIdAndDeletedDateIsNull(id) // 게시글 조회
        .awaitSingleOrNull() ?: throw PostNotFoundException()

    if (post.writer.id != authentication.id) throw PermissionDeniedException()

    val postJob = launch { // 동시성 처리
        postRepository.save(post.copy(deletedDate = now)) // 게시글 삭제 처리
            .awaitSingle()
    }
    val commentDeferred = async { // 동시성 처리
        commentRepository.findAllByPostIdAndDeletedDateIsNull(id) // 게시글에 달린 댓글 전체 조회
            .asFlow()
            .map { it.copy(deletedDate = now) }
            .map {
                commentRepository.save(it) // 댓글 삭제 처리
                    .awaitSingle()
            }
            .collect()
    }

    listOf(postJob, commentDeferred).joinAll()
}
```

코루틴은 비동기 및 논블로킹 코드를 명령형 프로그래밍 방식으로 작성할 수 있다는 장점을 가지고 있다.
위 코드를 보면 알겠지만 기존의 Reactor로 작성된 코드와 달리 굉장히 직관적인 코드를 가지고 있다.
기존의 `Mono`는 `awaitSingle()` 등의 일시 중지 함수로 실제 값을 가져올 수 있으며, 예외적으로 `Flux`는 Coroutine의 `Flow`로 변환해 사용한다.

Reactor와 비교해 코루틴이 단점이라고 할 수 있는 것은 Kotlin만 사용 가능하다는 제약 및 Spring WebFlux와의 호환성 문제가 있을텐데, 이는 최근에는 Spring 측에서 적극적으로
코루틴을 지원하고 있어 문제는 없다고 본다.

```kotlin
suspend fun <T : Any> ServerRequest.awaitBody(clazz: KClass<T>): T =
    bodyToMono(clazz.java).awaitSingle()

suspend inline fun <reified T : Any> ServerRequest.awaitBodyOrNull(): T? =
    bodyToMono<T>().awaitSingleOrNull()

suspend fun <T : Any> ServerRequest.awaitBodyOrNull(clazz: KClass<T>): T? =
    bodyToMono(clazz.java).awaitSingleOrNull()

suspend fun ServerRequest.awaitFormData(): MultiValueMap<String, String> =
    formData().awaitSingle()

suspend fun ServerRequest.awaitMultipartData(): MultiValueMap<String, Part> =
    multipartData().awaitSingle()
```

Spring WebFlux에서 코루틴을 지원하는 대표적인 예시로 `await` 접미사가 붙은 여러 확장 함수들이 있다.

```kotlin

@NoRepositoryBean
interface CoroutineCrudRepository<T, ID> : Repository<T, ID> {
    suspend fun <S : T> save(entity: S): T

    suspend fun findById(id: ID): T?

    fun findAll(): Flow<T>

    ...
}
```

```kotlin
abstract class CoWebExceptionHandler : WebExceptionHandler {
    final override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val context = exchange.attributes[CoWebFilter.COROUTINE_CONTEXT_ATTRIBUTE] as CoroutineContext?
        return mono(context ?: Dispatchers.Unconfined) { coHandle(exchange, ex) }.then()
    }

    protected abstract suspend fun coHandle(exchange: ServerWebExchange, ex: Throwable)
}
```

또한 아예 코루틴 전용 클래스(`CoroutineCrudRepository`, `WebExceptionHandler`의 변형 `CoWebExceptionHandler` 등)도 존재한다.
참고로 위 `CoWebExceptionHandler`는 필자가 [Contribution](https://github.com/spring-projects/spring-framework/pull/32931)을 통해 직접
만든 클래스로, 굉장히 최근에 추가된 것이다.

## Test

```kotlin
val result = StepVerifier.create(authService.login(createLoginRequest()))

result.expectSubscription()
    .assertNext {
        it.accessToken shouldBeEqual TOKEN
        it.refreshToken shouldBeEqual TOKEN
    }
    .verifyComplete()
```

테스트의 경우, Reactor에서 제공하는 `StepVerifier`를 활용한다.

## Example

본
예제는 [Java](https://github.com/earlgrey02/spring-webflux-example/tree/main/reactor/java-reactor), [Kotlin](https://github.com/earlgrey02/spring-webflux-example/tree/main/reactor/kotlin-reactor), [Kotlin(Coroutine)](https://github.com/earlgrey02/spring-webflux-example/tree/main/coroutine)
예제로 구성되어 있다.
시간이 날때마다 업데이트 할 예정.
