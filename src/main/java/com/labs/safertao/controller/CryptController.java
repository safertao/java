package com.labs.safertao.controller;

import com.labs.safertao.entity.CryptResponse;
import com.labs.safertao.entity.ResponsesSize;
import com.labs.safertao.entity.ValidationCryptError;
import com.labs.safertao.memory.InMemoryStorage;
import com.labs.safertao.service.CryptService;
import com.labs.safertao.service.CounterService;
import com.labs.safertao.validator.CryptValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/labs")
@ResponseStatus(HttpStatus.OK)
public class CryptController
{
    private static final Logger logger = LoggerFactory.getLogger(CryptController.class);
    private CryptService cryptService;
    private CryptValidator cryptValidator;
    private InMemoryStorage inMemoryStorage;
    private CounterService counterService;

    @Autowired
    public CryptController(CryptService cryptService, CryptValidator cryptValidator,
                           InMemoryStorage inMemoryStorage, CounterService service)
    {
        this.cryptService = cryptService;
        this.cryptValidator = cryptValidator;
        this.inMemoryStorage = inMemoryStorage;
        this.counterService = service;
    }

    @GetMapping("/crypt")
    public ResponseEntity<Object> cryptString(@RequestParam("mode") char mode, @RequestParam("message") String message)
    {
        if(inMemoryStorage.getDataStorage().containsKey(message))
        {
            counterService.incrementCounter();
            counterService.incrementSynchronizedCounter();
            return new ResponseEntity<>(inMemoryStorage.getSavedCryptResponse(message), HttpStatus.OK);
        }
        logger.info("validation");
        ValidationCryptError messageResponse = cryptValidator.validateMessage(message);
        ValidationCryptError modeResponse = cryptValidator.validateMode(mode);
        if(!modeResponse.getErrors().isEmpty())
        {
            messageResponse.addErrors(modeResponse.getErrors());
        }
        if(!messageResponse.getErrors().isEmpty())
        {
            messageResponse.setStatus(HttpStatus.BAD_REQUEST.name());
            logger.error("message argument is invalid");
            return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
        }
        String answer;
        try
        {
            logger.info("getting information");
            answer = cryptService.cryptMessage(mode, message);
        }
        catch(RuntimeException ex)
        {
            String error = "yesterday is forbidden word to encrypt(decrypt)";
            messageResponse.addError(error);
            messageResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
            logger.error(error);
            return new ResponseEntity<>(messageResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        CryptResponse response = new CryptResponse(mode, message, answer);
        inMemoryStorage.saveCryptResponse(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/responses")
    public ResponseEntity<Object> getAllCryptResponses()
    {
        return new ResponseEntity<>(inMemoryStorage.getAllSavedCryptResponses(), HttpStatus.OK);
    }

    @GetMapping("/responses/size")
    public ResponseEntity<Object> getAllCryptResponsesSize()
    {
        return new ResponseEntity<>(new ResponsesSize(inMemoryStorage.size()), HttpStatus.OK);
    }
}