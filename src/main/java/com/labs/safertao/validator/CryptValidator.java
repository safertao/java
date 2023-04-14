package com.labs.safertao.validator;

import com.labs.safertao.controller.CryptController;
import com.labs.safertao.entity.ValidationCryptError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

@Component
public class CryptValidator
{
    private static final Logger logger = LoggerFactory.getLogger(CryptController.class);

    public ValidationCryptError validateMode(char mode)
    {
        ValidationCryptError response = new ValidationCryptError();
        String error;
        if(!(mode == 'd' || mode == 'e'))
        {
            error = "mode has to be 'e'(encrypt) or 'd'(decrypt)";
            logger.error(error);
            response.addError(error);
        }
        return response;
    }

    public ValidationCryptError validateMessage(String message)
    {
        ValidationCryptError response = new ValidationCryptError();
        String error;
        if(message.isEmpty())
        {
            error = "message is empty";
            logger.error(error);
            response.addError(error);
        }
        if (message.length() > 30)
        {
            error = "message can't be longer than 30 chars";
            logger.error(error);
            response.addError(error);
        }
        if (!StringUtils.isAlpha(message) && !message.isEmpty())
        {
            error = "message must be alpha";
            logger.error(error);
            response.addError(error);
        }
        return response;
    }
}