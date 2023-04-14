package com.labs.safertao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.labs.safertao.service.inter.ICryptService;

@Service
public class CryptService implements ICryptService
{
    private CounterService counterService;

    @Autowired
    public CryptService(CounterService service)
    {
        counterService = service;
    }

    @Override
    public String cryptMessage(char mode, String message) throws RuntimeException
    {
        counterService.incrementCounter();
        counterService.incrementSynchronizedCounter();
        if(message.equals("yesterday")) throw new RuntimeException("yesterday is forbidden word to encrypt(decrypt)");
        if(message.equals("tomorrow")) return "";
        int encryptionNumber = 3;
        if(mode == 'd') encryptionNumber = -3;
        message = message.toLowerCase();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); i++)
        {
            int tmp = message.charAt(i) - 97;
            tmp = (tmp + encryptionNumber + 26) % 26 + 97;
            result.append((char) tmp);
        }
        return result.toString();
    }
}
