package com.feazesa.reactive.core;

import java.time.Duration;

import reactor.core.publisher.Flux;

public class Application {
    private static Integer min = 1;
    private static Integer max = 5;

    Flux<Integer> evenNumbers = Flux.range(min, max)
            .filter(x -> x % 2 == 0)
            ;
    Flux<Integer> oddNumbers = Flux.range(min, max).filter(x -> x % 2 > 0);

    public static void main(String[] args) {
       Application app = new Application();
        app.combineLatest2();
    }

    public Flux<Integer> combineLatest1() {
        // System.out.println(evenNumbers.subscribe(System.out::println));
        // System.out.println(oddNumbers.subscribe(System.out::println));

        Flux<Integer> fluxOfInteger = Flux.combineLatest(evenNumbers, oddNumbers, (a, b) -> a + b);
        System.out.println(fluxOfInteger.subscribe(System.out::println));
        return fluxOfInteger;
    }

    public void combineLast1() {

        Flux<Integer> x = Flux.combineLatest((n) -> (Integer) n[0] + (Integer) n[1], evenNumbers, oddNumbers);
        System.out.println(x.subscribe(System.out::println));

    }

    public void merge() {
        Flux<Integer> merge = Flux.merge(evenNumbers, oddNumbers);
        System.out.println(merge.subscribe(System.out::println));
    }

    public void concat() {
        Flux<Integer> concat = Flux.concat(evenNumbers, oddNumbers);
        System.out.println(concat.subscribe(System.out::println));
    }

    public void concatWith() {
        Flux<Integer> concatWith = evenNumbers.concatWith(oddNumbers);
        System.out.println(concatWith.subscribe(System.out::println));
    }

    public void combineLatest() {
        Flux<Integer> combineLatest = Flux.combineLatest(obj -> ((int) obj[0] + (int) obj[1]), oddNumbers, evenNumbers);
        combineLatest.subscribe(System.out::println);

    }
    
    public void combineLatest2() {
        Flux<Integer> combineLatest = Flux.combineLatest(
                evenNumbers
                
                , 
                oddNumbers
//              .delayElements(Duration.ofMillis(200L))

                , 
                (a, b) -> {
                    System.out.println(a);
                    System.out.println(b);
                    return a + b;
                    })
                
//                .log()
                ;
        combineLatest.subscribe(System.out::println);

    }

}
