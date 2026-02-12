package com.africom.correspmanage.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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


}
