package com.labs.safertao.controller;

import com.labs.safertao.database.CryptEntity;
import com.labs.safertao.database.DataBaseService;
import com.labs.safertao.entity.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
    private DataBaseService dataBaseService;
    private CryptEntity cryptEntity;

    @Autowired
    public CryptController(CryptService cryptService, CryptValidator cryptValidator, InMemoryStorage inMemoryStorage,
                           CounterService service, DataBaseService dataBaseService, CryptEntity cryptEntity)
    {
        this.cryptService = cryptService;
        this.cryptValidator = cryptValidator;
        this.inMemoryStorage = inMemoryStorage;
        this.counterService = service;
        this.dataBaseService = dataBaseService;
        this.cryptEntity = cryptEntity;
    }

    @GetMapping("/crypt")
    public ResponseEntity<CryptResponse> cryptString(@RequestParam("mode") char mode, @RequestParam("message") String message)
    {
        String status = null;
        InputPair pair = new InputPair(mode, message);
        CryptEntity dbEntity;
        if((dbEntity = dataBaseService.getCryptEntity(mode, message)) != null)
        {
            logger.info("data was in database already");
            counterService.incrementCounter();
            counterService.incrementSynchronizedCounter();
            CryptResponse response = new CryptResponse(dbEntity.getMode(), dbEntity.getMessage(),
                    dbEntity.getAnswer(), new ValidationCryptError(), HttpStatus.OK.name());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if(inMemoryStorage.getSavedCryptResponse(pair) != null)
        {
            logger.info("data was in memory storage already");
            counterService.incrementCounter();
            counterService.incrementSynchronizedCounter();
            CryptResponse tmp = inMemoryStorage.getSavedCryptResponse(pair);
            CryptResponse response = new CryptResponse(tmp.mode(), tmp.message(), tmp.answer(), tmp.errors(), tmp.status());
            return new ResponseEntity<>(response, HttpStatus.valueOf(tmp.status()));
        }
        logger.info("validation");
        ValidationCryptError errors = cryptValidator.validateMessage(message);
        ValidationCryptError modeErrors = cryptValidator.validateMode(mode);
        if(!modeErrors.getErrors().isEmpty())
        {
            errors.addErrors(modeErrors.getErrors());
            status = HttpStatus.BAD_REQUEST.name();
        }
        if(!errors.getErrors().isEmpty())
        {
            if(status == null) status = HttpStatus.BAD_REQUEST.name();
            logger.error("message argument is invalid");
            CryptResponse response = new CryptResponse(mode, message, "", errors, status);
            inMemoryStorage.saveCryptResponse(response);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        String answer;
        try
        {
            logger.info("getting information");
            answer = cryptService.cryptMessage(mode, message);
            status = HttpStatus.OK.name();
            CryptResponse response = new CryptResponse(mode, message, answer, errors, status);
            inMemoryStorage.saveCryptResponse(response);
            dataBaseService.saveCrypt(response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(RuntimeException ex)
        {
            String error = "yesterday is forbidden word to encrypt(decrypt)";
            errors.addError(error);
            status = HttpStatus.INTERNAL_SERVER_ERROR.name();
            logger.error(error);
            CryptResponse response = new CryptResponse(mode, message, error, errors, status);
            inMemoryStorage.saveCryptResponse(response);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @PostMapping("/crypt")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BulkCryptResponse> cryptBulkStrings(@RequestBody List<CryptBulkParameters> cryptList)
    {
        List<CryptResponse> result = new ArrayList<>();
        final HttpStatus[] resultStatus = {HttpStatus.CREATED};
        cryptList.forEach(e ->
        {
            ResponseEntity<CryptResponse> response = cryptString(e.mode(), e.message());
            CryptResponse res = response.getBody();
            if(!Objects.equals(Objects.requireNonNull(res).status(), HttpStatus.OK.name()))
            {
                resultStatus[0] = HttpStatus.valueOf(res.status());
            }
            result.add(res);
        });
        logger.info("successful post mapping");

        String minLengthString = cryptService.minLengthString(result);
        String maxLengthString = cryptService.maxLengthString(result);
        Integer avgLength = cryptService.avgLength(result);

        return new ResponseEntity<>(new BulkCryptResponse(result, minLengthString,
                                    maxLengthString, avgLength), resultStatus[0]);
    }

    @GetMapping("/getdb")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CryptEntity>> getDB()
    {
        return new ResponseEntity<>(dataBaseService.getAllCrypts(), HttpStatus.OK);
    }

    @GetMapping("/getbyid")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CryptEntity> getById(long id)
    {
        return ResponseEntity.ok(dataBaseService.getById(id));
    }

    @GetMapping("/async")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> asyncCryptString(@RequestParam("mode") char mode,
                                                   @RequestParam("message") String message)
    {
        String status = null;
        CryptEntity dbEntity;
        if((dbEntity = dataBaseService.getCryptEntity(mode, message)) != null)
        {
            logger.info("result was in db already");
            counterService.incrementCounter();
            counterService.incrementSynchronizedCounter();
            Integer id = (Integer) dbEntity.getId();
            if(id == null) id = 0;
            AsyncEntity response = new AsyncEntity("result was in db already", (long) id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        logger.info("validation");
        ValidationCryptError errors = cryptValidator.validateMessage(message);
        ValidationCryptError modeErrors = cryptValidator.validateMode(mode);
        if(!modeErrors.getErrors().isEmpty())
        {
            errors.addErrors(modeErrors.getErrors());
            status = HttpStatus.BAD_REQUEST.name();
        }
        if(!errors.getErrors().isEmpty())
        {
            if(status == null) status = HttpStatus.BAD_REQUEST.name();
            logger.error("message argument is invalid");
            CryptResponse response = new CryptResponse(mode, message, "", errors, status);
            inMemoryStorage.saveCryptResponse(response);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try
        {
            CompletableFuture.runAsync(() ->
            {
                String answer = cryptService.cryptMessage(mode, message);
                CryptResponse resp = new CryptResponse(mode, message, answer, errors, HttpStatus.OK.name());
                inMemoryStorage.saveCryptResponse(resp);
                dataBaseService.saveCrypt(resp);
            });
            long nextId = dataBaseService.size() + 1;
            return ResponseEntity.ok(new AsyncEntity("added to db with predefined id", nextId));
        }
        catch(RuntimeException ex)
        {
            String error = "yesterday is forbidden word to encrypt(decrypt)";
            errors.addError(error);
            status = HttpStatus.INTERNAL_SERVER_ERROR.name();
            logger.error(error);
            CryptResponse response = new CryptResponse(mode, message, error, errors, status);
            inMemoryStorage.saveCryptResponse(response);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}