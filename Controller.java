package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.api;

import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateRequest;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateResponse;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.service.GreetingClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.util.LogBuilder.logBuilder;
import static org.slf4j.event.Level.INFO;

/** The Rest API controller of the Application */
//@RestController handles the request from the client
@RestController
public class Controller {
/** Logger is used to provide log messages like (Info, Error,
 * Debug, Warning) at class level.
 */
  private Logger logger = LoggerFactory.getLogger(getClass());

  /** creating the instance variable as GreetingClientService
   * making it private ,so it can only be accessed within the same class*/
  private GreetingClientService greetingClientService;

/** parameterized constructor is created to initialize the GreetingClientServer*/
  public Controller(GreetingClientService greetingClientService) {
    this.greetingClientService = greetingClientService;
  }

  /** This method runs in the path "/api/request" and it accepts the
   * parameters from the client and creates a greetingRequest*/
  @GetMapping(value = "/api/request")
  /** using @RequestParam we can extract the firstName and lastName*/
  public ResponseEntity<GreetingCreateRequest> request(@RequestParam String firstName, @RequestParam String lastName) {
    GreetingCreateRequest greetingCreateRequest = greetingClientService.requestGreeting(firstName, lastName);

     /**Building custom logs with logBuilder class*/
    logBuilder()
            .loggerName(getClass())
            .level(INFO)
            .message("Create greeting create request")
            .parameter("transactionId", greetingCreateRequest.getTransactionId())
            .parameter("requestTime", greetingCreateRequest.getRequestTime())
            .parameter("firstName", greetingCreateRequest.getFirstName())
            .parameter("lastName", greetingCreateRequest.getLastName())
            .build();

    return ResponseEntity.ok(greetingCreateRequest);
  }

  /** This method will run at the path api/response and creates a greetingResponse based on the key*/
  @GetMapping(value = "/api/response")
  public ResponseEntity<GreetingCreateResponse> response(@RequestParam String key) {
    GreetingCreateResponse greetingCreateResponse = greetingClientService.greetingCreateResponseByKey(key);

    /** Building custom logs with LogBuilder class */
    logBuilder()
            .loggerName(getClass())
            .level(INFO)
            .message("Lookup greeting create response by key")
            .parameter("key", key)
            .build();

    return ResponseEntity.ok(greetingCreateResponse);
  }


}
