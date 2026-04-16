package com.africom.correspmanage.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ORGACARE")
public interface OrgaCareRestClient {
    @GetMapping("/api/departement-code-with-children/{deptCode}")
    List<String> getDepartementAndChildrenCodes(@PathVariable(name = "deptCode") String deptCode);

    @GetMapping("/api/departement-code-with-parents/{deptCode}")
    List<String> getDepartementAndParentsCodes(@PathVariable(name = "deptCode") String deptCode);

    @GetMapping("/api/departements/map-parents-by-code/{deptCode}")
    Map<String, List<String>> getMapDepartementParentsByCode(@PathVariable(name = "deptCode") String deptCode);

    @GetMapping("/api/departements-nom-by-code/{deptCode}")
    String getDepartementNomByCode(@PathVariable(name = "deptCode") String deptCode);

//    @GetMapping("/api/departements/by-user/{userId}/codes")
//    List<String> getDepartementCodesByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/api/departements/user/{userId}/affectations")
    List<Map<String, Object>> getDepartementAffectationsByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/api/departements/by-code/{code}")
    Map<String, Object> getDepartementDtoByCode(@PathVariable("code") String code);

    // Récupérer map codeDept -> nomDepartement (bulk)
    @PostMapping("/api/departements/names-by-codes")
    Map<String, String> getDepartementNamesByCodes(@RequestBody List<String> deptCodes);

    // Récupérer map matricule -> nomPrenom (bulk)
    @PostMapping("/api/personnes/names-by-matricules")
    Map<String, String> getPersonneNamesByMatricules(@RequestBody List<String> matricules);

    @GetMapping("/api/personnes/by-matricule/{matricule}")
    Map<String, Object> getPersonneByMatricule(@PathVariable("matricule") String matricule);

}
