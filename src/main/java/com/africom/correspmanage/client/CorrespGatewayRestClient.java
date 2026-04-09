package com.africom.correspmanage.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "CORRESPCARE")
public interface CorrespGatewayRestClient {

    /**
     * Appelle : GET /api/admin/users/by-login?login={login}
     * Retourne 200 + UserDTO ou 404 (Feign lèvera une exception si 404 selon config).
     */
    @GetMapping(value = "/api/admin/users/by-login")
    UserFeignDTO findUserByLogin(@RequestParam("login") String login);

    /**
     * Appelle : POST /api/admin/users/by-logins
     * Body: ["login1","login2"]
     * Retourne la liste des users trouvés.
     */
    @PostMapping(value = "/api/admin/users/by-logins", consumes = "application/json")
    List<UserFeignDTO> findUsersByLogins(@RequestBody List<String> logins);

    @GetMapping(value = "/api/admin/users/id/{id}")
    Map<String, Object> getUserById(@PathVariable("id") Long id);

    @GetMapping(value = "/api/admin/users/me/email-password", produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> getCurrentUserEmailPassword();


}
