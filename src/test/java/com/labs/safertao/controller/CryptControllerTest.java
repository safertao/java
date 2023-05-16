package com.labs.safertao.controller;

import com.labs.safertao.entity.*;
import com.labs.safertao.memory.InMemoryStorage;
import com.labs.safertao.service.CounterService;
import com.labs.safertao.service.CryptService;
import com.labs.safertao.validator.CryptValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CryptControllerTest {
    @Mock
    private CryptService cryptService;

    @Mock
    private CryptValidator cryptValidator;

    @Mock
    private InMemoryStorage inMemoryStorage;

    @Mock
    private CounterService counterService;

    @InjectMocks
    private CryptController cryptController = new CryptController(cryptService, cryptValidator, inMemoryStorage, counterService);

    @Test
    public void testCryptString() {
        String message = "wxhvgdb";
        char mode = 'd';
        String answer = "tuesday";
        String status = HttpStatus.OK.name();
        when(cryptService.cryptMessage(mode, message)).thenReturn(answer);
        when(cryptValidator.validateMessage(message)).thenReturn(new ValidationCryptError());
        when(cryptValidator.validateMode(mode)).thenReturn(new ValidationCryptError());
        doNothing().when(inMemoryStorage).saveCryptResponse(new CryptResponse(mode,
                                        message, answer, new ValidationCryptError(), status));
        ResponseEntity<CryptResponse> response = cryptController.cryptString(mode, message);
        CryptResponse result = response.getBody();

        verify(inMemoryStorage, times(1)).saveCryptResponse(any(CryptResponse.class));
        assertNotNull(response);
        assertEquals(answer, Objects.requireNonNull(result).answer());
    }

    @Test
    public void testCryptStringServiceError() {
        String message = "yesterday";
        char mode = 'd';
        String answer = "yesterday is forbidden word to encrypt(decrypt)";
        String status = HttpStatus.INTERNAL_SERVER_ERROR.name();
        ValidationCryptError errors = new ValidationCryptError();
        doNothing().when(inMemoryStorage).saveCryptResponse(new CryptResponse(mode,
                message, answer, errors, status));
        when(cryptValidator.validateMessage(message)).thenReturn(errors);
        when(cryptValidator.validateMode(mode)).thenReturn(errors);
        when(cryptService.cryptMessage(mode, message)).thenThrow(RuntimeException.class);

        ResponseEntity<CryptResponse> response = cryptController.cryptString(mode, message);
        ValidationCryptError result = Objects.requireNonNull(response.getBody()).errors();

        verify(inMemoryStorage, times(1)).saveCryptResponse(any(CryptResponse.class));
        assertThrows(RuntimeException.class, () -> cryptService.cryptMessage(mode, message));
        assertNotNull(result);
        assertEquals(answer, Objects.requireNonNull(result).getErrors().get(0));
    }

    @Test
    public void testCryptStringValidationError() {
        String message = "abcdefjhijklmnopqrstuvwxyz123456789";
        char mode = 'a';
        String answer = "";
        String status = HttpStatus.BAD_REQUEST.name();
        ValidationCryptError errors = new ValidationCryptError();
        errors.addError("message can't be longer than 30 chars");
        errors.addError("message must be alpha");
        doNothing().when(inMemoryStorage).saveCryptResponse(new CryptResponse(mode,
                message, answer, errors, status));
        when(cryptValidator.validateMessage(message)).thenReturn(errors);
        errors.addError("mode has to be 'e'(encrypt) or 'd'(decrypt)");
        when(cryptValidator.validateMode(mode)).thenReturn(errors);

        ResponseEntity<CryptResponse> response = cryptController.cryptString(mode, message);
        verify(inMemoryStorage, times(1)).saveCryptResponse(any(CryptResponse.class));

        ValidationCryptError result = Objects.requireNonNull(response.getBody()).errors();
        assertNotNull(result);
        assertEquals(errors, result);
    }

    @Test
    public void testGetAllSavedResponses() {
        when(inMemoryStorage.getAllSavedCryptResponses()).thenReturn(null);

        ResponseEntity<Object> response = cryptController.getAllCryptResponses();
        Object result = response.getBody();
        assertNull(result);
    }

    @Test
    public void testGetAllSavedResponsesSize() {
        when(inMemoryStorage.size()).thenReturn(0);

        ResponseEntity<Object> response = cryptController.getAllCryptResponsesSize();
        ResponsesSize result = (ResponsesSize) response.getBody();
        assertEquals(new ResponsesSize(0), result);
    }

    @Test
    public void testCryptStringWithInMemoryStorage() {
        String message = "wxhvgdb";
        char mode = 'd';
        String answer = "tuesday";
        String status = HttpStatus.OK.name();
        ValidationCryptError errors = new ValidationCryptError();
        CryptResponse savedResponse = new CryptResponse(mode, message, answer, errors, status);

        when(inMemoryStorage.getSavedCryptResponse(message)).thenReturn(savedResponse);
        ResponseEntity<CryptResponse> response = cryptController.cryptString(mode, message);
        CryptResponse result = response.getBody();

        assertNotNull(result);
        assertEquals(result.answer(), answer);
    }

    @Test
    public void testCryptBulkStrings() {
        List<CryptBulkParameters> cryptList = new ArrayList<>();
        String message1 = "wxhvgdb";
        char mode1 = 'd';
        String message2 = "hohoho";
        char mode2 = 'h';
        String answer1 = "tuesday";
        String answer2 = "";
        ValidationCryptError errors1 = new ValidationCryptError();
        ValidationCryptError errors2 = new ValidationCryptError();
        errors2.addError("mode has to be 'e'(encrypt) or 'd'(decrypt)");
        String status1 = HttpStatus.OK.name();
        String status2 = HttpStatus.BAD_REQUEST.name();
        String minLengthString = "";
        String maxLengthString = "tuesday";
        Integer avgLength = 3;
        CryptResponse res1 = new CryptResponse(mode1, message1, answer1, errors1, status1);
        CryptResponse res2 = new CryptResponse(mode2, message2, answer2, errors2, status2);
        List<CryptResponse> resultList = new ArrayList<>();

        cryptList.add(new CryptBulkParameters(mode1, message1));
        cryptList.add(new CryptBulkParameters(mode2, message2));
        resultList.add(res1);
        resultList.add(res2);

        when(inMemoryStorage.getSavedCryptResponse(message1)).thenReturn(null);
        when(inMemoryStorage.getSavedCryptResponse(message2)).thenReturn(null);
        doNothing().when(inMemoryStorage).saveCryptResponse(res1);
        doNothing().when(inMemoryStorage).saveCryptResponse(res2);
        when(cryptService.cryptMessage(mode1, message1)).thenReturn(answer1);
        when(cryptValidator.validateMessage(message1)).thenReturn(errors1);
        when(cryptValidator.validateMode(mode1)).thenReturn(errors1);
        when(cryptValidator.validateMessage(message2)).thenReturn(errors2);
        when(cryptValidator.validateMode(mode2)).thenReturn(errors2);

        when(cryptService.minLengthString(resultList)).thenReturn(minLengthString);
        when(cryptService.maxLengthString(resultList)).thenReturn(maxLengthString);
        when(cryptService.avgLength(resultList)).thenReturn(avgLength);

        ResponseEntity<BulkCryptResponse> response = cryptController.cryptBulkStrings(cryptList);
        BulkCryptResponse result = response.getBody();

        verify(inMemoryStorage, times(2)).saveCryptResponse(any(CryptResponse.class));
        assertNotNull(result);
        assertEquals(resultList, result.responses());
        assertEquals(avgLength, result.avgLength());
        assertEquals(minLengthString, result.minLengthString());
        assertEquals(maxLengthString, result.maxLengthString());
    }
}