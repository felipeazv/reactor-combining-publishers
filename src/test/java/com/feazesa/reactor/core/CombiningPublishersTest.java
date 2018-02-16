package com.feazesa.reactor.core;

import static org.junit.Assert.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import reactor.core.Fuseable;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


public class CombiningPublishersTest {
    
    private static Integer min = 1;
    private static Integer max = 5;

    private static Flux<Integer> evenNumbers = Flux.range(min, max).filter(x -> x % 2 == 0);
    private static Flux<Integer> oddNumbers = Flux.range(min, max).filter(x -> x % 2 > 0);
    
    
    @Test
    public void testMerge() {
        Flux<Integer> fluxOfIntegers = Flux.merge(
                evenNumbers, 
                oddNumbers);
        
        StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
    }
    
    @Test
    public void testMergeWithDelayedElements() {
        Flux<Integer> fluxOfIntegers = Flux.merge(
                evenNumbers.delayElements(Duration.ofMillis(500L)), 
                oddNumbers.delayElements(Duration.ofMillis(300L)));
        
        StepVerifier.create(fluxOfIntegers)
        .expectNext(1)
        .expectNext(2)
        .expectNext(3)
        .expectNext(5)
        .expectNext(4)
        .expectComplete()
        .verify();
    }
    
    @Test
    public void testConcat() {
        Flux<Integer> fluxOfIntegers = Flux.concat(
                evenNumbers.delayElements(Duration.ofMillis(500L)), 
                oddNumbers.delayElements(Duration.ofMillis(300L)));
        
        StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
    }
    
    @Test
    public void testConcatWith() {
        Flux<Integer> fluxOfIntegers = evenNumbers
                .concatWith(oddNumbers);
        
        StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
    }
    
    @Test
    public void testCombineLatest() {
        Flux<Integer> fluxOfIntegers = Flux.combineLatest(
                evenNumbers, 
                oddNumbers, 
                (a, b) -> a + b);

        StepVerifier.create(fluxOfIntegers)
        .expectNext(5)
        .expectNext(7)
        .expectNext(9)
        .expectComplete()
        .verify();

    }
    
    @Test
    public  void fused() {
            DirectProcessor<Integer> dp1 = DirectProcessor.create();
            DirectProcessor<Integer> dp2 = DirectProcessor.create();

            Flux.combineLatest(dp1, dp2, (a, b) -> a + b);

            dp1.onNext(1);
            dp1.onNext(2);

            dp2.onNext(10);
            dp2.onNext(20);
            dp2.onNext(30);

            dp1.onNext(3);

            dp1.onComplete();
            dp2.onComplete();

         
    }
    
    @Test
    public void combineLatestStreamData1() {
//          "Combine latest stream data"
//          given: "source composables to combine, buffer and tap"
           
//          when: "the sources are combined"
            Flux<String> mergedFlux =
                            Flux.combineLatest(evenNumbers, oddNumbers , (a , b) ->  " " + a + b);
            List<String> res = new ArrayList<>();

            mergedFlux.subscribe(
                            it -> {
                                    res.add(it);
                                    System.out.println(it);
                            }, Throwable::printStackTrace,
                            () -> {
                                    Collections.sort(res);
                                    res.add("done");
                                    System.out.println("completed!");
                            });

//            w1.onNext(1);
//            w2.onNext(2);
////            w3.onNext("3a");
//            w1.onComplete();
//            // twice for w2
//            w2.onNext(3);
//            w2.onComplete();
            // 4 times for w3
//            w3.onNext("3b");
//            w3.onNext("3c");
//            w3.onNext("3d");
//            w3.onComplete();

    }
    
    @Test
    public void combineLatestStreamData() {
//          "Combine latest stream data"
//          given: "source composables to combine, buffer and tap"
            EmitterProcessor<String> w1 = EmitterProcessor.create();
            EmitterProcessor<String> w2 = EmitterProcessor.create();
            EmitterProcessor<String> w3 = EmitterProcessor.create();

//          when: "the sources are combined"
            Flux<String> mergedFlux =
                            Flux.combineLatest(w1, w2, w3, t -> "" + t[0] + t[1] + t[2]);
            List<String> res = new ArrayList<>();

            mergedFlux.subscribe(
                            it -> {
                                    res.add(it);
                                    System.out.println(it);
                            }, Throwable::printStackTrace,
                            () -> {
                                    Collections.sort(res);
                                    res.add("done");
                                    System.out.println("completed!");
                            });

            w1.onNext("1a");
            w2.onNext("2a");
            w3.onNext("3a");
            w1.onComplete();
            // twice for w2
            w2.onNext("2b");
            w2.onComplete();
            // 4 times for w3
            w3.onNext("3b");
            w3.onNext("3c");
            w3.onNext("3d");
            w3.onComplete();

    }
    

    @Test
    public void testCombineLatest1() {
            StepVerifier.create(Flux.combineLatest(obj -> (int) obj[1], evenNumbers, oddNumbers))
            .expectNext(1)
            .expectNext(3)
            .expectNext(5)
                        .verifyComplete();
    }
    
    @Test
    public void testMergeSequential() {
        Flux<Integer> fluxOfIntegers = Flux.mergeSequential(
                evenNumbers, 
                oddNumbers);
        
        StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
    }
    
    
    @Test
    public void testMergeDelayError() {
        Flux<Integer> fluxOfIntegers = Flux.mergeDelayError(1, 
                evenNumbers.delayElements(Duration.ofMillis(500L)), 
                oddNumbers.delayElements(Duration.ofMillis(300L)));
        
        StepVerifier.create(fluxOfIntegers)
        .expectNext(1)
        .expectNext(2)
        .expectNext(3)
        .expectNext(5)
        .expectNext(4)
        .expectComplete()
        .verify();
    }
    
    @Test
    public void testMergeWith() {
        Flux<Integer> fluxOfIntegers = evenNumbers.mergeWith(oddNumbers);
        
        StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
    }
    
    @Test
    public void testZip() {
        Flux<Integer> fluxOfIntegers = Flux.zip(
                evenNumbers, 
                oddNumbers, 
                (a, b) -> a + b);
        
        StepVerifier.create(fluxOfIntegers)
        .expectNext(3)
        .expectNext(7)
        .expectComplete()
        .verify();
    }
    
    @Test
    public void testZipWith() {
        Flux<Integer> fluxOfIntegers = evenNumbers
                .zipWith(oddNumbers, 
                (a, b) -> a * b);
        
        StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(12)
        .expectComplete()
        .verify();
    }
    
    
}
