package com.trustedsolutions.crypto.repository;

import com.trustedsolutions.crypto.model.TrustedDevice;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.repository.CrudRepository;

@EntityScan(basePackages = {"com.trustedsolutions.crypto.models"})
public interface TrustedDeviceRepository extends CrudRepository<TrustedDevice, Long> {

    TrustedDevice findTrustedDeviceById(Long id);

    TrustedDevice findTrustedDeviceByDevicePublicId(String devicePublicId);
    
    TrustedDevice findTrustedDeviceByDevicePrivateId(String devicePrivateId);

    boolean existsTrustedDeviceByDevicePublicId(String devicePublicId);


}
