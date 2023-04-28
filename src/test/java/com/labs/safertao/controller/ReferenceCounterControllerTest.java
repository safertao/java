package com.labs.safertao.controller;


import com.labs.safertao.entity.ReferenceCounters;
import com.labs.safertao.service.CounterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReferenceCounterControllerTest
{
    @Mock
    private CounterService counterService;

    @InjectMocks
    private ReferenceCounterController controller = new ReferenceCounterController(counterService);

    @Test
    public void testReferenceCount()
    {
        when(counterService.getCounter()).thenReturn(0);
        when(counterService.getSynchronizedCounter()).thenReturn(0);

        ResponseEntity<Object> response = controller.referenceCount();
        ReferenceCounters result = (ReferenceCounters) response.getBody();
        assertNotNull(result);
        assertEquals(0, result.counter());
        assertEquals(0, result.synchronizedCounter());
    }

    @Test
    public void testClearReferenceCount()
    {
        doNothing().when(counterService).clear();

        controller.clearReferenceCount();

        verify(counterService, times(1)).clear();
    }
}
