package base;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.time.Duration;


@Configuration
public class CircuitBreakerTest {
    

    @Bean(name = "circuitBreakerRegistry")
    public CircuitBreakerRegistry circuitBreakerRegistry() {
//        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
//                .failureRateThreshold(6)
//                .slowCallDurationThreshold(Duration.ofSeconds(5))
//                .minimumNumberOfCalls(2)
//                .waitDurationInOpenState(Duration.ofSeconds(10))
//                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
//                .slidingWindowSize(1000)
//                .build();
        return CircuitBreakerRegistry.ofDefaults();

    }
    
    
    @Bean(name = "testCirBreaker1")
    public CircuitBreaker circuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(30)
                .slowCallDurationThreshold(Duration.ofSeconds(35))
                .minimumNumberOfCalls(10)
                .waitDurationInOpenState(Duration.ofSeconds(8))
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(1000)
                .build();
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("testCirBreaker1", config);
        //CircuitBreaker circuitBreaker = CircuitBreaker.of("testCirBreaker", config);
        //CircuitBreakerRegistry.of("testCirBreaker").circuitBreaker()
        
        return circuitBreaker;
        
    }
    
    @Bean(name = "testCirBreaker2")
    public CircuitBreaker circuitBreaker2(CircuitBreakerRegistry circuitBreakerRegistry) {
        
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(60)
                .slowCallDurationThreshold(Duration.ofSeconds(35))
                .minimumNumberOfCalls(10)
                .waitDurationInOpenState(Duration.ofSeconds(6))
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                .slidingWindowSize(2000)
                .build();
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("testCirBreaker2", config);
        //CircuitBreaker circuitBreaker = CircuitBreaker.of("testCirBreaker", config);
        //CircuitBreakerRegistry.of("testCirBreaker").circuitBreaker()
        
        return circuitBreaker;
        
    }
    
    
}
