/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trustedsolutions.crypto.repository;


import com.trustedsolutions.crypto.model.Company;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.repository.CrudRepository;

@EntityScan(basePackages = {"com.trustedsolutions.crypto.models"})
public interface CompanyRepository extends CrudRepository<Company, Long> {

    Company findCompanyById(Long id);

}
