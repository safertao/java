package com.labs.safertao.service;

import org.springframework.stereotype.Service;

@Service
public class CounterService
{
    private Integer synchronizedCounter;
    private Integer counter;

    public void clear()
    {
        synchronizedCounter = 0;
        counter = 0;
    }

    public synchronized void incrementSynchronizedCounter()
    {
        synchronizedCounter++;
    }

    public void incrementCounter()
    {
        counter++;
    }

    public CounterService()
    {
        this.synchronizedCounter = 0;
        this.counter = 0;
    }

    public CounterService(Integer synchronizedCounter, Integer counter) {
        this.synchronizedCounter = synchronizedCounter;
        this.counter = counter;
    }

    public Integer getSynchronizedCounter()
    {
        return synchronizedCounter;
    }

    public Integer getCounter()
    {
        return counter;
    }

}
