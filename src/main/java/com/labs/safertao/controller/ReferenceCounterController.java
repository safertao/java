package com.labs.safertao.controller;

import com.labs.safertao.entity.ReferenceCounters;
import com.labs.safertao.service.CounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/references")
public class ReferenceCounterController
{
    private CounterService counterService;
    private static final Logger logger = LoggerFactory.getLogger(CryptController.class);


    @Autowired
    public ReferenceCounterController(CounterService service)
    {
        counterService = service;
    }

    @GetMapping("/count")
    public ResponseEntity<Object> referenceCount()
    {
        logger.info("returning reference counters...");
        return new ResponseEntity<>(new ReferenceCounters(counterService.getSynchronizedCounter(),
                                    counterService.getCounter()), HttpStatus.OK);
    }

    @GetMapping("/clear")
    public void clearReferenceCount()
    {
        logger.info("clearing reference counters...");
        counterService.clear();
    }
}









