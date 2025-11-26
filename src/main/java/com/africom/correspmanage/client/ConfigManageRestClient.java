package com.africom.correspmanage.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CONFIGMANAGE")
public interface ConfigManageRestClient {
    @GetMapping("/api/typedocuments/{id}")
    TypeDocumentFeignDTO getTypeDocumentById(@PathVariable(name = "id") Long typeDocumentId);
}
