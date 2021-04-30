/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trustedsolutions.crypto.controller;

import com.trustedsolutions.crypto.forms.CompanyForm;
import com.trustedsolutions.crypto.forms.CompanyTrdustedDeviceAssignForm;
import com.trustedsolutions.crypto.forms.UserCheckForm;
import com.trustedsolutions.crypto.model.Company;
import com.trustedsolutions.crypto.model.TrustedDevice;
import com.trustedsolutions.crypto.repository.CompanyRepository;
import com.trustedsolutions.crypto.repository.TrustedDeviceRepository;
import com.trustedsolutions.crypto.services.ParameterStringBuilder;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CompaniesController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    TrustedDeviceRepository trustedDeviceRepository;

    /*
    POST /companies/add - добавление компании
    POST /companies/user_check - проверка принадлежности пользователя компании

    GET /companies - список активных компаний
    GET /companies/{id} - получение информации о компании
    PUT /companies/update - редактирование компании

    POST /companies/attach - ассоциирование доверенных устройств с компанией
    POST /companies/detach - убирает доверенное устройств из компанией
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/companies/add",
            method = RequestMethod.POST,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> add(@RequestBody CompanyForm companyForm) {

        Company company = new Company(companyForm);
        company = (Company) companyRepository.save(company);

        return new ResponseEntity<>(company.toJSON(), HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/companies/update",
            method = RequestMethod.POST,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> update(@RequestBody CompanyForm companyForm) {
        System.out.println(companyForm.getDescription());

        Company company = companyRepository.findCompanyById(companyForm.getId());

        if (company == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }
        company.setCompany(companyForm);

        companyRepository.save(company);

        return new ResponseEntity<>(company.toJSON(), HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/companies/user_check",
            method = RequestMethod.POST,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> checkUser(@RequestBody UserCheckForm userCheckForm) throws MalformedURLException, ProtocolException, IOException, ParseException {

        Company company = companyRepository.findCompanyById(userCheckForm.getCompanyId());

        if (company == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }

        URL url = new URL(company.getUserCheckUrl());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("userId", userCheckForm.getUserId());

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();

        int status = con.getResponseCode();

        if (status != HttpStatus.OK.value()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        JSONParser jp = new JSONParser();
        JSONObject message = (JSONObject) jp.parse(content.toString());

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/companies",
            method = RequestMethod.GET,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> companies() {

        List<Company> companies = (List<Company>) companyRepository.findAll();

        JSONArray array = new JSONArray();
        for (Company company : companies) {
            array.add(company.toJSON());
        }

        return new ResponseEntity<>(array, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/companies/{companyId:[0-9]{1,100}}",
            method = RequestMethod.GET,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> get(@PathVariable Long companyId) {

        Company company = companyRepository.findCompanyById(companyId);

        if (company == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }

        return new ResponseEntity<>(company.toJSON(), HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/companies/attach",
            method = RequestMethod.POST,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> attach(@RequestBody CompanyTrdustedDeviceAssignForm companyTrdustedDeviceAssignForm) {

        Company company = companyRepository.findCompanyById(companyTrdustedDeviceAssignForm.getCompanyId());

        if (company == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }

        TrustedDevice device = trustedDeviceRepository.findTrustedDeviceById(companyTrdustedDeviceAssignForm.getTrustedDeviceId());

        if (device == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }

        company.getTrustedDevices().add(device);

        companyRepository.save(company);

        return new ResponseEntity<>(company.toJSON(), HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/companies/detach",
            method = RequestMethod.POST,
            headers = {"X-API-VERSION=0.0.3", "content-type=application/json"})
    @ResponseBody
    public ResponseEntity<Object> detach(@RequestBody CompanyTrdustedDeviceAssignForm companyTrdustedDeviceAssignForm) {

        Company company = companyRepository.findCompanyById(companyTrdustedDeviceAssignForm.getCompanyId());

        if (company == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }

        TrustedDevice device = trustedDeviceRepository.findTrustedDeviceById(companyTrdustedDeviceAssignForm.getTrustedDeviceId());

        if (device == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("http.status.code.404",
                            null, LocaleContextHolder.getLocale())
            );
        }
        company.getTrustedDevices().remove(device);

        companyRepository.save(company);

        return new ResponseEntity<>(company.toJSON(), HttpStatus.OK);
    }

}
