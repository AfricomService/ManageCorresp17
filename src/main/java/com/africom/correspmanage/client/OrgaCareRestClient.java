package com.africom.correspmanage.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.*;

@AuthorizedFeignClient(name = "ORGACARE")
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
    List<Map<String, Object>> getDepartementAffectationsByUserId(@PathVariable("userId") String userId);

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

    @GetMapping("/api/affectations/emails-by-departement-and-type")
    List<String> getEmailsByDepartementAndType(
        @RequestParam("departementId") Long departementId,
        @RequestParam("type") String type);

    @GetMapping("/api/societes/by-matricule/{matricule}")
    List<Map<String, Object>> getSocietesByMatricule(@PathVariable("matricule") String matricule);

    // Sociétés de l'utilisateur connecté (token relayé par AuthorizedFeignClient)
    @GetMapping("/api/societes/mes-societes")
    List<Map<String, Object>> getMesSocietes();

    @GetMapping("/api/societes/list")
    List<Map<String, Object>> getAllSocietesIdAndRaisonSociale();

    @GetMapping("/api/departements/nom-by-matricule/{matricule}")
    String getDepartementNomByMatricule(@PathVariable("matricule") String matricule);

    @GetMapping("/api/departements/by-matricule/{matricule}")
    Map<String, Object> getDepartementByMatricule(@PathVariable("matricule") String matricule);

    // Arbre limité aux départements de l'utilisateur + leurs sous-départements (dans l'organigramme donné)
    @GetMapping("/api/departements/tree-by-user/{organigrammeCode}")
    List<Map<String, Object>> getMyDepartementTree(
        @PathVariable("organigrammeCode") String organigrammeCode,
        @RequestParam("userId") String userId);

}
