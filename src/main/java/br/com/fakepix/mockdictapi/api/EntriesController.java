package br.com.fakepix.mockdictapi.api;

import br.com.fakepix.mockdictapi.domain.model.directory.CreateEntryRequest;
import br.com.fakepix.mockdictapi.domain.model.directory.DeleteEntryRequest;
import br.com.fakepix.mockdictapi.domain.model.directory.DirectoryService;
import br.com.fakepix.mockdictapi.domain.model.directory.EntryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class EntriesController {
  
  public static final String ENTRIES = "entries";
  public static final String PI_PAYER_ACCOUNT_SERVICER = "PI-PayerAccountServicer";
  public static final String PI_PAYER_ID = "PI-PayerId";
  public static final String PI_END_TO_END_ID = "PI-EndToEndId";
  public static final String PI_RATE_LIMIT_CLIENT_REMAINING = "PI-RateLimit-ClientRemaining";
  public static final String PI_RATE_LIMIT_CLIENT_RESET = "PI-RateLimit-ClientReset";
  public static final String PI_RATE_LIMIT_PARTICIPANT_REMAINING = "PI-RateLimit-ParticipantRemaining";
  public static final String PI_RATE_LIMIT_PARTICIPANT_RESET = "PI-RateLimit-ParticipantReset";
  public static final String PI_RATE_LIMIT_CLIENT_REMAINING_VALUE = "100";
  public static final String PI_RATE_LIMIT_CLIENT_RESET_VALUE = "30";
  public static final String PI_RATE_LIMIT_PARTICIPANT_REMAINING_VALUE = "100";
  public static final String PI_RATE_LIMIT_PARTICIPANT_RESET_VALUE = "30";
  
  private DirectoryService directoryService;
  
  @Autowired
  public EntriesController(DirectoryService directoryService) {
    this.directoryService = directoryService;
  }
  
  @RequestMapping(path = ENTRIES, method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
  @ResponseStatus(code = HttpStatus.CREATED)
  public HttpEntity createEntry(@RequestBody CreateEntryRequest request) {
    try {
      return new ResponseEntity(directoryService.create(request), HttpStatus.CREATED);
    } catch (ResponseException e) {
      return new ResponseEntity(e.getProblem(), HttpStatus.resolve(e.getProblem().getStatus()));
    }
  }
  
  @RequestMapping(path = ENTRIES + "/{key}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity retrieveEntry(@RequestHeader(name = PI_PAYER_ACCOUNT_SERVICER) String payerAccount,
                                             @RequestHeader(name = PI_PAYER_ID) String payerId,
                                             @RequestHeader(name = PI_END_TO_END_ID) String e2eID,
                                             @PathVariable(name = "key") String key) {
  
    HttpHeaders headers = new HttpHeaders();
    headers.set(PI_RATE_LIMIT_CLIENT_REMAINING, PI_RATE_LIMIT_CLIENT_REMAINING_VALUE);
    headers.set(PI_RATE_LIMIT_CLIENT_RESET, PI_RATE_LIMIT_CLIENT_RESET_VALUE);
    headers.set(PI_RATE_LIMIT_PARTICIPANT_REMAINING, PI_RATE_LIMIT_PARTICIPANT_REMAINING_VALUE);
    headers.set(PI_RATE_LIMIT_PARTICIPANT_RESET, PI_RATE_LIMIT_PARTICIPANT_RESET_VALUE);
  
    try {
      return ResponseEntity.ok().headers(headers).body(directoryService.retrieveEntry(key));
    } catch (EntryNotFoundException e) {
      return new ResponseEntity(e.getProblem(), HttpStatus.resolve(e.getProblem().getStatus()));
    }
  }

  @RequestMapping(path = ENTRIES + "/{key}/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public HttpEntity  deleteEntry(@RequestBody DeleteEntryRequest request){

    try {
      directoryService.deleteEntry(request);
      return new ResponseEntity(HttpStatus.NO_CONTENT);
    }catch (ResponseException e){
      return new ResponseEntity(e.getProblem(), HttpStatus.resolve(e.getProblem().getStatus()));
    }

  }


}
