/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trustedsolutions.crypto.controller;

import com.core.cryptointerface.EncryptService;
import com.core.cryptointerface.TelegramLoggerService;
import com.core.cryptointerface.UserPayloadServiceForCrypt;
import com.core.cryptointerface.forms.InfoRequestForm;
import com.core.cryptointerface.forms.ResponseTDPublicIdForm;
import com.core.cryptointerface.forms.TransferDataForm;
import com.core.cryptointerface.forms.TrustedDeviceForm;
import com.core.cryptolib.enums.InfoRequestType;

import com.trustedsolutions.crypto.model.TrustedDevice;
import com.trustedsolutions.crypto.repository.TrustedDeviceRepository;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class TrustedDeviceController {

    /*
    POST    /trusted_devices/add - добавление нового устройства
    GET     /trusted_devices/check - проверка наличия доверенного устройста по публичному ключу
    POST    /trusted_devices/reencrypt - перешифрование данных
    PUT     /trusted_devices/update - обновление параметров устройства
    GET     /trusted_devices/refresh/{id} - перегенерация ключей устройства по id
    GET     /trusted_devices/get/{id} - получение публичного ключа по  идентификатору устройства
    
     */
    @Autowired
    TrustedDeviceRepository tdRepository;

    @Autowired
    MessageSource messageSource;

    EncryptService desApp;

    @Value("${app.title}")
    private String appTitle;

    @Value("${telegram.logger-channel}")
    private String telegramChannel;

    @Value("${telegram.logger-token}")
    private String telegramToken;

    TelegramLoggerService logger;

    public TrustedDeviceController() {
        this.logger = new TelegramLoggerService(
                telegramChannel,
                telegramToken,
                appTitle,
                TrustedDeviceController.class.getName(),
                true);

        this.desApp = new EncryptService();
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/trusted_devices/check/{trudtedDevicePublicId:[0-9]{1,100}}",
            method = RequestMethod.GET,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> check(@PathVariable("trudtedDevicePublicId") String trustedDevicePublicId) throws UnsupportedEncodingException {

        boolean isExist = tdRepository.existsTrustedDeviceByDevicePublicId(trustedDevicePublicId);

        JSONObject message = new JSONObject();
        message.put("is_exist", isExist);

        UserPayloadServiceForCrypt ups = new UserPayloadServiceForCrypt();

        if (!isExist) {

            InfoRequestForm infoRequestForm = new InfoRequestForm();

            infoRequestForm.setId(1l);
            infoRequestForm.setRecipientUserId("testid");
            infoRequestForm.setSenderUserId("testid");

            return new ResponseEntity<>(ups.denailRequest().toBase64SimpleJSON(),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/trusted_devices/add",
            method = RequestMethod.POST,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> add(@RequestBody TrustedDeviceForm trustedDevice) {

        TrustedDevice td = new TrustedDevice(trustedDevice);

        if (td == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }

        TrustedDevice tdAdded = (TrustedDevice) tdRepository.save(td);

        JSONObject obj = new JSONObject();
        obj.put("id", tdAdded.getId());

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/trusted_devices/update",
            method = RequestMethod.PUT,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> update(@RequestBody TrustedDeviceForm trustedDevice) {

        TrustedDevice td = tdRepository.findTrustedDeviceById(trustedDevice.getId());

        if (td == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }

        td.setTrustedDevice(trustedDevice);

        tdRepository.save(td);

        return new ResponseEntity<>(td.toJSON(), HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @RequestMapping(value = "/trusted_devices/refresh/{publicDeviceId:[a-zA-Z0-9-_]{2,512}}",
            method = RequestMethod.GET,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> refresh(@PathVariable("publicDeviceId") String publicDeviceId) throws NoSuchAlgorithmException {
        TrustedDevice td = tdRepository.findTrustedDeviceByDevicePublicId(publicDeviceId);

        if (td == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }
        //todo: добавить проверку принадлежности устройства компании, а компании ключу
        byte[] tmp_actual = EncryptService.getSecureRandom(8);
        byte[] tmp_old = td.getDeviceActualKey();

        td.setDeviceActualKeyEncode(tmp_actual);
        td.setDeviceOldKeyEncode(tmp_old);
        tdRepository.save(td);

        return new ResponseEntity<>(td.toJSON(), HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/trusted_devices/get/{deviceId:[a-zA-Z0-9-_]{2,512}}",
            method = RequestMethod.GET,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> get(@PathVariable("deviceId") String trustedDevicePublicId) {
        TrustedDevice td = tdRepository.findTrustedDeviceByDevicePublicId(trustedDevicePublicId);

        if (td == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }
        return new ResponseEntity<>(td.toJSON(), HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping(value = "/trusted_devices/reencrypt",
            method = RequestMethod.POST,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> reencrypt(@RequestBody InfoRequestForm infoRequestForm) throws ParseException, UnsupportedEncodingException, InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException {

//todo: на вхол принимаются юзеры и необходимо сделать првоерку юзеров сперва infoRequestForm.getRecipientUserId() и infoRequestForm.getSenderUserId()
        byte[] decrypted_data = Base64.getDecoder().decode(infoRequestForm.getData().getBytes());

        UserPayloadServiceForCrypt ups = new UserPayloadServiceForCrypt();

        JSONObject tmp = null;
        try {
            JSONParser parser = new JSONParser();
            tmp = (JSONObject) parser.parse(new String(decrypted_data));

        } catch (ParseException ex) {

            logger.info(String.format("%s\n*Data*:\n%s",
                    ex.getMessage(),
                    infoRequestForm.getData()
            ));

            String tmpData = infoRequestForm.getData();
            infoRequestForm.setData(ups.denailRequest().toBase64SimpleJSON());

            return new ResponseEntity<>(infoRequestForm.toJSON(),
                    HttpStatus.OK);
        }

        TransferDataForm tdf = new TransferDataForm();
        tdf.setDataBase64((String) tmp.get("data"));
        tdf.setType(Integer.parseInt(tmp.get("type").toString()));

        try {

            
            //todo: ПРОВЕРИТЬ ПРИВЕДЕНИЕ ТИПОВ
            ResponseTDPublicIdForm resp = (ResponseTDPublicIdForm) ups.twiceEncryptedPermissionBegin(tdf);

            if (resp == null) {
                tdf = (TransferDataForm) ups.denailRequest();
            } else {

                TrustedDevice tdRecipient = tdRepository.findTrustedDeviceByDevicePublicId(
                        resp.getTdRecipientTrustedDevicePublicId());

                TrustedDevice tdSender = tdRepository.findTrustedDeviceByDevicePublicId(
                        resp.getTdSenderTrustedDevicePublicId());

                byte[] senderDeviceNewKey = EncryptService.getSecureRandom(8);

                byte[] recipientDeviceNewKey = EncryptService.getSecureRandom(8);

                tdf = (TransferDataForm) ups.twiceEncryptedPermissionEnd(
                        tdRecipient.getTrustedDeviceForm(),
                        tdSender.getTrustedDeviceForm(),
                        senderDeviceNewKey,
                        recipientDeviceNewKey
                );

                byte[] tmp_recipient_actual_key = tdRecipient.getDeviceActualKey();
                byte[] tmp_sender_actual_key = tdSender.getDeviceActualKey();

                tdRecipient.setDeviceActualKeyEncode(recipientDeviceNewKey);
                tdRecipient.setDeviceOldKeyEncode(tmp_recipient_actual_key);
                tdRepository.save(tdRecipient);

                tdSender.setDeviceActualKeyEncode(senderDeviceNewKey);
                tdSender.setDeviceOldKeyEncode(tmp_sender_actual_key);
                tdRepository.save(tdSender);

            }

        } catch (Exception ex) {
            logger.info(String.format("%s\n*Data*:\n%s",
                    ex.getMessage(),
                    infoRequestForm.getData()
            ));
            String tmpData = infoRequestForm.getData();
            infoRequestForm.setData(ups.denailRequest().toBase64SimpleJSON());

            return new ResponseEntity<>(infoRequestForm.toJSON(),
                    HttpStatus.OK);
        }

        if (tdf.getType() == InfoRequestType.denial.getValue()) {

            logger.info(String.format("*Current type*=%d *needed type*=%d\n*Data*:\n%s",
                    tdf.getType(),
                    InfoRequestType.twiceEncryptedRequest.getValue(),
                    infoRequestForm.getData()
            ));
            String tmpData = infoRequestForm.getData();
            infoRequestForm.setData(ups.denailRequest().toBase64SimpleJSON());

            return new ResponseEntity<>(infoRequestForm.toJSON(),
                    HttpStatus.OK);
        }
        /*сохраняем новые ключи устройства*/

        infoRequestForm.setData(Base64
                .getEncoder()
                .encodeToString(tdf.toJSON()
                        .toJSONString().getBytes()));

        return new ResponseEntity<>(infoRequestForm.toJSON(), HttpStatus.OK);
    }
}
