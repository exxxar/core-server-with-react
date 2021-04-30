package com.trustedsolutions.crypto.controller;


import com.trustedsolutions.crypto.model.Transfer;
import com.trustedsolutions.crypto.repository.TrustedDeviceRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;

@RestController
public class CryptographicController {
    @Autowired
    MessageSource messageSource;

    @Autowired
    TrustedDeviceRepository trustedDeviceRepository;

    // test
    @GetMapping(value = "/", headers = {"X-API-VERSION=1", "content-type=application/json"})
    public Object hello() {
        return new ResponseEntity<>("test", HttpStatus.OK);
    }

    @PostMapping(value = "/reencrypt", headers = {"content-type=application/json", "X-API-VERSION=0.0.1"})
    public Object reencrypt(@RequestBody Transfer transfer){
        if (transfer.getSenderUserId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    messageSource.getMessage("transfer.error.sender_trusted_device_public_id_is_required",
                            null, LocaleContextHolder.getLocale())
            );
        }

        if (transfer.getRecipientUserId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    messageSource.getMessage("transfer.error.recipient_trusted_device_public_id_is_required",
                            null, LocaleContextHolder.getLocale())
            );
        }

        if (transfer.getData() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    messageSource.getMessage("transfer.error.data_is_required",
                            null, LocaleContextHolder.getLocale())
            );
        }

        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }
}
