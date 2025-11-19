package com.africom.correspmanage.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ORGACAREAPP")
public interface OrgaCareRestClient {
    @GetMapping("/api/departement-code-with-children/{deptCode}")
    List<String> getDepartementAndChildrenCodes(@PathVariable(name = "deptCode") String deptCode);

    @GetMapping("/api/departement-code-with-parents/{deptCode}")
    List<String> getDepartementAndParentsCodes(@PathVariable(name = "deptCode") String deptCode);
}
