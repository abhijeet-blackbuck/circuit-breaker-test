package base;

import com.newrelic.api.agent.Trace;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping(value = "v1/test")
public class HelloController {
    
    
    private AtomicInteger reqCountForC1 = new AtomicInteger(0);
    
    private AtomicInteger reqCountForC2 = new AtomicInteger(0);
    
    
    @RequestMapping("/index")
    @Trace(dispatcher = true)
    public ResponseEntity<String> index() {
        
        int random0 = new Random().nextInt(6);
        
        
        
        switch (random0) {
            
            case 3:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR " + reqCountForC1.get());
            case 1:

                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            case 2:
                return ResponseEntity.status(HttpStatus.OK).body("OK");
            default:
                return ResponseEntity.status(HttpStatus.OK).body("OK");
            
        }
        
    }
    
    
    @RequestMapping("/index2")
    @Trace(dispatcher = true)
    public ResponseEntity<String> index2() {
        int random = new Random().nextInt(8);

        switch (random) {

            case 7:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
            case 1:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");
            case 2:
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("TIMEOUT");
            default:
                return ResponseEntity.status(HttpStatus.OK).body("OK");

        }
    }
    
    
}
