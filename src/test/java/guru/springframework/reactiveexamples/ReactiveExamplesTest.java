package guru.springframework.reactiveexamples;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Créé par dmitri le 2022-07-04.
 */
@Slf4j
public class ReactiveExamplesTest {

    Person michael = new Person("Michael", "Weston");
    Person fiona = new Person("Fiona", "Glenanne");
    Person sam = new Person("Sam", "Axe");
    Person jesse = new Person("Jesse", "Porter");

    @Test
    public void monoTests() throws Exception {
        //create new person mono
        Mono<Person> personMono = Mono.just(michael);

        //get person object from mono publisher
        StepVerifier.create(personMono.log())
                .consumeNextWith(person -> {
                    assertThat(person.sayMyName()).isEqualTo("My Name is Michael Weston.");
                })
                .verifyComplete();
    }

    @Test
    public void monoTransform() throws Exception {
        //create new person mono
        Mono<PersonCommand> personCommandMono = Mono.just(fiona)
                .map(person -> { //type transformation
                    return new PersonCommand(person);
                });

        StepVerifier.create(personCommandMono.log())
                .consumeNextWith(command -> {
                    assertThat(command.sayMyName()).isEqualTo("My Name is Fiona Glenanne.");
                })
                .verifyComplete();
    }

    @Test
    public void monoFilter() {
        Mono<Person> personMono = Mono.just(sam)
                .filter(person -> person.getFirstName().equalsIgnoreCase("foo"));

        //filter example
        StepVerifier.create(personMono.log())
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void fluxTest() {

        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.subscribe(person -> log.info(person.sayMyName()));
        StepVerifier.create(people.log())
                .consumeNextWith(michael -> assertThat(michael.sayMyName()).isEqualTo("My Name is Michael Weston."))
                .consumeNextWith(fiona -> assertThat(fiona.sayMyName()).isEqualTo("My Name is Fiona Glenanne."))
                .consumeNextWith(sam -> assertThat(sam.sayMyName()).isEqualTo("My Name is Sam Axe."))
                .consumeNextWith(jesse -> assertThat(jesse.sayMyName()).isEqualTo("My Name is Jesse Porter."))
                .verifyComplete();
    }

    @Test
    public void fluxTestFilter() {

        Flux<Person> filteredPeople = Flux.just(michael, fiona, sam, jesse)
                .filter(person -> person.getFirstName().equals(fiona.getFirstName()));

        StepVerifier.create(filteredPeople.log())
                .expectSubscription()
                .expectNext(fiona)
                .verifyComplete();
    }

    public void fluxTestDelayWithOutput() {

        Flux<Person> people = Flux.just(michael, fiona, sam, jesse)
                .delayElements(Duration.ofSeconds(1));

        StepVerifier.create(people.log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void fluxTestDelayNoOutput() {

        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.delayElements(Duration.ofSeconds(1))
                .subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    public void fluxTestDelay() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.delayElements(Duration.ofSeconds(1))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        countDownLatch.await();

    }

    @Test
    public void fluxTestFilterDelay() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.delayElements(Duration.ofSeconds(1))
                .filter(person -> person.getFirstName().contains("i"))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        countDownLatch.await();
    }

    @Test
    public void fluxTestFilterDelay2() {

        Flux<Person> people = Flux.just(michael, fiona, sam, jesse)
                .delayElements(Duration.ofSeconds(1))
                .filter(person -> person.getFirstName().contains("i"));

        StepVerifier.create(people.log())
                .consumeNextWith(michael -> assertThat(michael.sayMyName()).isEqualTo("My Name is Michael Weston."))
                .consumeNextWith(fiona -> assertThat(fiona.sayMyName()).isEqualTo("My Name is Fiona Glenanne."))
                .verifyComplete();
    }
}
