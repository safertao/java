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
    public ResponseEntity<CryptResponse> cryptString(@RequestParam("mode") char mode, @RequestParam("message") String message)
    {
        if(inMemoryStorage.getSavedCryptResponse(message) != null)
        {
            counterService.incrementCounter();
            counterService.incrementSynchronizedCounter();
            return new ResponseEntity<>(inMemoryStorage.getSavedCryptResponse(message), HttpStatus.OK);
        }
        logger.info("validation");
        ValidationCryptError errors = cryptValidator.validateMessage(message);
        ValidationCryptError modeErrors = cryptValidator.validateMode(mode);
        if(!modeErrors.getErrors().isEmpty())
        {
            errors.addErrors(modeErrors.getErrors());
            errors.setStatus(HttpStatus.BAD_REQUEST.name());
        }
        if(!errors.getErrors().isEmpty())
        {
            if(errors.getStatus() == null) errors.setStatus(HttpStatus.BAD_REQUEST.name());
            logger.error("message argument is invalid");
            CryptResponse response = new CryptResponse(mode, message, "", errors);
            inMemoryStorage.saveCryptResponse(response);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
            errors.addError(error);
            errors.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
            logger.error(error);
            CryptResponse response = new CryptResponse(mode, message, "", errors);
            inMemoryStorage.saveCryptResponse(response);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        errors.setStatus(HttpStatus.OK.name());
        CryptResponse response = new CryptResponse(mode, message, answer, errors);
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


/*

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

@Controller
@ControllerAdvice
public class CalculatorController {
    private static final Logger logger = LogManager.getLogger(CalculatorController.class);
    @Autowired
    private CalculatorLogic calculatorLogic;

    @GetMapping("/calculator")
    public String calculateParams(@RequestParam(name = "number", required = false, defaultValue = "0") int number,
                                  @RequestParam(name = "action", required = false, defaultValue = "empty") String action,
                                  Model model) throws IllegalArgumentsException {
        CalculableParameters requestParameters = new CalculableParameters();
        requestParameters.setAction(action);
        requestParameters.setNumber(number);
        Integer result = calculatorLogic.calculateResult(requestParameters);

        Synchronization.semaphore.release();

        model.addAttribute("message", "Результат: " + result);
        model.addAttribute("number", result);
        logger.info("Successfully getMapping");
        return "home";
    }

    @PostMapping("/calculator")
    public ResponseEntity<?> calculateBulkParams(@Valid @RequestBody List<CalculableParameters> bodyList) {

        List<Integer> resultList = new LinkedList<>();
        bodyList.forEach((currentElement) -> {
            try {
                resultList.add(calculatorLogic.calculateResult(currentElement));
            } catch (IllegalArgumentsException e) {
                logger.error("Error in postMapping");
            }
        });

        logger.info("Successfully postMapping");
        int sumResult = calculatorLogic.calculateSumOfResult(resultList);
        int maxResult = calculatorLogic.findMaxOfResult(resultList);
        int minResult = calculatorLogic.findMinOfResult(resultList);

        return new ResponseEntity<>(resultList + "\nSum: " + sumResult + "\nMax result: " +
                maxResult + "\nMin result: " + minResult, HttpStatus.OK);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String handlerException() {
        logger.info("handlerException");
        return ("/error/400.html");
    }
}*/