package com.africom.correspmanage.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "CONFIGMANAGE")
public interface ConfigManageRestClient {
    @GetMapping("/api/typedocuments/{id}")
    TypeDocumentFeignDTO getTypeDocumentById(@PathVariable(name = "id") Long typeDocumentId);

    @GetMapping("/api/parent-classdefs/next/{type}")
    String getNextNumber(@PathVariable(name = "type") String type);

    @GetMapping("/api/model-notifs/{id}")
    ModelNotifDTO  getModelNotifById(@PathVariable(name = "id") UUID id);

    @GetMapping("/api/model-notifs/by-code/{code}")
    ModelNotifDTO getModelNotifByCode(@PathVariable("code") String code);


}
