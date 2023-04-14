package com.labs.safertao.controller;

import com.labs.safertao.entity.CryptResponse;
import com.labs.safertao.entity.ResponsesSize;
import com.labs.safertao.entity.ValidationCryptError;
import com.labs.safertao.memory.InMemoryStorage;
import com.labs.safertao.service.CounterService;
import com.labs.safertao.service.CryptService;
import com.labs.safertao.validator.CryptValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CryptControllerTest
{
    @Mock
    private CryptService cryptService;

    @Mock
    private CryptValidator cryptValidator;

    @Mock
    private InMemoryStorage inMemoryStorage;

    @Mock
    private CounterService counterService;

    @InjectMocks
    private CryptController cryptController = new CryptController(cryptService, cryptValidator,
                                                                  inMemoryStorage, counterService);

    @Test
    public void testCryptString()
    {
        String message = "wxhvgdb";
        char mode = 'd';
        String answer = "tuesday";

        when(cryptService.cryptMessage(mode, message)).thenReturn(answer);
        when(cryptValidator.validateMessage(message)).thenReturn(new ValidationCryptError());
        when(cryptValidator.validateMode(mode)).thenReturn(new ValidationCryptError());

        ResponseEntity<Object> response = cryptController.cryptString(mode, message);
        CryptResponse result = (CryptResponse) response.getBody();
        assertNotNull(response);
        assertEquals(answer, Objects.requireNonNull(result).answer());
    }

    @Test
    public void testCryptStringServiceError()
    {
        String message = "yesterday";
        char mode = 'd';
        String answer = "yesterday is forbidden word to encrypt(decrypt)";

        when(cryptValidator.validateMessage(message)).thenReturn(new ValidationCryptError());
        when(cryptValidator.validateMode(mode)).thenReturn(new ValidationCryptError());
        when(cryptService.cryptMessage(mode, message)).thenThrow(RuntimeException.class);

        ResponseEntity<Object> response = cryptController.cryptString(mode, message);
        ValidationCryptError result = (ValidationCryptError) response.getBody();

        assertThrows(RuntimeException.class, () -> cryptService.cryptMessage(mode, message));
        assertNotNull(result);
        assertEquals(answer, Objects.requireNonNull(result).getErrors().get(0));
    }

    @Test
    public void testCryptStringValidationError()
    {
        String message = "abcdefjhijklmnopqrstuvwxyz123456789";
        char mode = 'a';

        ValidationCryptError errors = new ValidationCryptError();
        errors.addError("message can't be longer than 30 chars");
        errors.addError("message must be alpha");
        when(cryptValidator.validateMessage(message)).thenReturn(errors);

        errors.addError("mode has to be 'e'(encrypt) or 'd'(decrypt)");
        when(cryptValidator.validateMode(mode)).thenReturn(errors);

        ResponseEntity<Object> response = cryptController.cryptString(mode, message);
        ValidationCryptError result = (ValidationCryptError) response.getBody();
        assertNotNull(result);
        assertEquals(errors, result);
    }

    @Test
    public void testGetAllSavedResponses()
    {
        when(inMemoryStorage.getAllSavedCryptResponses()).thenReturn(null);

        ResponseEntity<Object> response = cryptController.getAllCryptResponses();
        Object result = response.getBody();
        assertNull(result);
    }

    @Test
    public void testGetAllSavedResponsesSize()
    {
        when(inMemoryStorage.size()).thenReturn(0);

        ResponseEntity<Object> response = cryptController.getAllCryptResponsesSize();
        ResponsesSize result = (ResponsesSize) response.getBody();
        assertEquals(new ResponsesSize(0), result);
    }
}

