package com.labs.safertao.controller;

import com.labs.safertao.entity.HitCounters;
import com.labs.safertao.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/tests")
public class JmeterController
{
    private CounterService counterService;

    @Autowired
    public JmeterController(CounterService service)
    {
        counterService = service;
    }

    @GetMapping("/hitcount")
    public ResponseEntity<Object> hitCount()
    {
        return new ResponseEntity<>(new HitCounters(counterService.getSynchronizedCounter(),
                                    counterService.getCounter()), HttpStatus.OK);
    }

    @GetMapping("/hitcount/clear")
    public void clearHitCount()
    {
        counterService.clear();
    }
}









