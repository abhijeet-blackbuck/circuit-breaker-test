package base;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.newrelic.NewRelicMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CircuitBreakerMetrics {
    

    private MeterRegistry meterRegistry;

    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    @Autowired
    public void setMeterRegistry(NewRelicMeterRegistry meterRegistry) {

        this.meterRegistry = meterRegistry;
    }
    
    
    @Autowired
    public void setCircuitBreakerRegistry(CircuitBreakerRegistry circuitBreakerRegistry) {

        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }


    /*
     * This will bind the circuit breaker metrics to micrometer.
     */
    @Bean
    public TaggedCircuitBreakerMetrics getCircuitBreakerMetrics() {

        TaggedCircuitBreakerMetrics taggedCircuitBreakerMetrics = TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(circuitBreakerRegistry);
        taggedCircuitBreakerMetrics.bindTo(meterRegistry);
        return taggedCircuitBreakerMetrics;
    }
    
    
}
