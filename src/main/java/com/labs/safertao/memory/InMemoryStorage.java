package com.labs.safertao.memory;

import com.labs.safertao.entity.CryptResponse;
import com.labs.safertao.entity.InputPair;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Component
public class InMemoryStorage
{
    private Map<InputPair, CryptResponse> dataStorage = new HashMap<InputPair, CryptResponse>();

    public synchronized void saveCryptResponse(CryptResponse response)
    {
        dataStorage.put(new InputPair(response.mode(), response.message()), response);
    }

    public CryptResponse getSavedCryptResponse(InputPair key)
    {
        return dataStorage.get(key);
    }

    public Integer size()
    {
        return dataStorage.size();
    }

    public Collection<CryptResponse> getAllSavedCryptResponses()
    {
        return dataStorage.values();
    }

    public Map<InputPair, CryptResponse> getDataStorage() { return dataStorage; }

}
