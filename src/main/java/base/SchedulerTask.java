package base;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnCallNotPermittedEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnErrorEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnIgnoredErrorEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnResetEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnSuccessEvent;
import io.github.resilience4j.core.EventConsumer;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Component
public class SchedulerTask {
    
    
    @Autowired
    @Qualifier("testCirBreaker1")
    private CircuitBreaker circuitBreaker1;
    
    
    @Autowired
    @Qualifier("testCirBreaker2")
    private CircuitBreaker circuitBreaker2;
    
    AtomicInteger atomicInteger = new AtomicInteger(0);
    
    Executor executors = Executors.newFixedThreadPool(8);
    
    @Trace(dispatcher = true)
    public ResponseEntity<String> callApi() {
        
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange("http://localhost:8080/v1/test/index", HttpMethod.GET, null, String.class);
    }
    
    @Trace(dispatcher = true)
    public ResponseEntity<String> callApi2() {
        
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange("http://localhost:8080/v1/test/index2", HttpMethod.GET, null, String.class);
    }
    
    
    @Scheduled(fixedRate = 50)
    @Trace(dispatcher = true)
    public void executeTask1() {
        //System.out.println("not permitted calls: "+circuitBreaker.getMetrics().getNumberOfNotPermittedCalls() +"failed calls: "+ circuitBreaker.getMetrics().getNumberOfFailedCalls());
        executors.execute(() -> c1());
        
    }
    
    
    @Scheduled(fixedRate = 100)
    @Trace(dispatcher = true)
    public void executeTask2() {
        NewRelic.incrementCounter("task_2");
        //System.out.println("not permitted calls: "+circuitBreaker.getMetrics().getNumberOfNotPermittedCalls() +"failed calls: "+ circuitBreaker.getMetrics().getNumberOfFailedCalls());
        executors.execute(() -> c2());
    }
    
    @Trace(dispatcher = true)
    public void c1() {
        
        Supplier<ResponseEntity> fetchTargetPicture = () -> this.callApi();
        fetchTargetPicture = CircuitBreaker
                .decorateSupplier(circuitBreaker1, fetchTargetPicture);
        ResponseEntity responseEntity = Try.ofSupplier(fetchTargetPicture)
                .recover(throwable -> def1(throwable)).get();
        //System.out.println("C1 " + "  " + responseEntity.getBody().toString() + " " + circuitBreaker.getState());
    }
    
    
    public ResponseEntity<String> def1(Throwable ignored) {
    
        
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ignored.getMessage());
    }
    
    @Trace(dispatcher = true)
    public void c2() {
        Supplier<ResponseEntity> fetchTargetPicture = () -> this.callApi2();
        fetchTargetPicture = CircuitBreaker
                .decorateSupplier(circuitBreaker2, fetchTargetPicture);
        ResponseEntity responseEntity = Try.ofSupplier(fetchTargetPicture)
                .recover(throwable -> def2()).get();
        
//        if(atomicInteger.get() >= 5 && circuitBreaker.getState().equals(CircuitBreaker.State.CLOSED)) {
//            System.out.println(circuitBreaker.getState());
//            circuitBreaker.transitionToOpenState();
//            System.out.println(circuitBreaker.getState());
//        }
        
        //System.out.println("C2 " + "  " + responseEntity.getBody().toString() + " " + circuitBreaker.getState());
    }
    
    @Trace(dispatcher = true)
    public ResponseEntity<String> def2() {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("CIRCUIT_OPEN");
    }
    
}
