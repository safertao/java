package com.labs.safertao.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CryptServiceTest
{
    private CounterService counterService = new CounterService();
    private CryptService cryptService = new CryptService(counterService);

    @Test
    public void testCrypt()
    {
        String message = "monday";
        char mode = 'e';
        String result = cryptService.cryptMessage(mode, message);
        assertNotNull(result);
        assertEquals(6, result.length());
        assertEquals("prqgdb", result);
    }

    @Test
    public void testEmptyCrypt()
    {
        String message = "tomorrow";
        char mode = 'd';
        String result = cryptService.cryptMessage(mode, message);
        assertEquals(0, result.length());
    }

    @Test
    public void testException() throws UnknownError
    {
        String message = "yesterday";
        char mode = 'e';
        Throwable thrown = assertThrows(RuntimeException.class,
                () -> cryptService.cryptMessage(mode, message));
        assertNotNull(thrown.getMessage());
    }
}



