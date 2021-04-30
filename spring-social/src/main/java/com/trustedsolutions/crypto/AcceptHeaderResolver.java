/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trustedsolutions.crypto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 *
 * @author SAMS
 */


public class AcceptHeaderResolver extends AcceptHeaderLocaleResolver {

       @Autowired
    MessageSource messageSource;

    @Value("${project.languagues}")
    private String languagues;

//    List<Locale> LOCALES = Arrays.asList(new Locale("ru"),
//            new Locale("es"),
//            new Locale("fr"),
//            new Locale("es", "MX"),
//            new Locale("zh"),
//            new Locale("en"));
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("Accept-Language");

        String tmp_lang_list[] = languagues.split(",");

        boolean langExist = Arrays.asList(tmp_lang_list).contains(headerLang);

        if (!langExist||headerLang.length()>3) 
           headerLang = (new Locale("en")).toLanguageTag();
             
        List<Locale> locales = new ArrayList<Locale>();

        for (String lang : tmp_lang_list) {
            locales.add(new Locale(lang));
        }

        if (tmp_lang_list.length == 0) {
            locales.add(new Locale("en"));
        }

        return headerLang == null || headerLang.isEmpty()
                ? Locale.getDefault()
                : Locale.lookup(Locale.LanguageRange.parse(headerLang), locales);
    }

}
