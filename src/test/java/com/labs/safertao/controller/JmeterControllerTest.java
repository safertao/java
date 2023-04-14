package com.labs.safertao.controller;


import com.labs.safertao.service.CounterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JmeterControllerTest
{
    @Mock
    private CounterService counterService;

    @InjectMocks
    private JmeterController controller = new JmeterController(counterService);

    @Test
    public void testHitCount()
    {
        when(counterService.getCounter()).thenReturn(0);
        when(counterService.getSynchronizedCounter()).thenReturn(0);

        ResponseEntity<Object> response = controller.hitCount();
        CounterService result = (CounterService) response.getBody();
        assertNotNull(result);
        assertEquals(0, result.getCounter());
        assertEquals(0, result.getSynchronizedCounter());
    }
}
