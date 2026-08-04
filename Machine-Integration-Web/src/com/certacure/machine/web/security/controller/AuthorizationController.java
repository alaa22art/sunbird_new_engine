package com.certacure.machine.web.security.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.certacure.core.common.business.exception.BusinessException;
import com.certacure.core.common.business.exception.BusinessException.ErrorSeverity;
import com.certacure.core.common.util.SecurityUtil;
import com.certacure.core.common.util.TokenUtil;
import com.certacure.lis.interfaces.admin.model.SecUser;
import com.certacure.lis.interfaces.admin.service.SecUserService;

/** AuthorizationController.java **/
@RestController
@RequestMapping("/services")
public class AuthorizationController {

    @Autowired
    private SecUserService userService;

    @RequestMapping(value= "/generateBranchedToken.srvc", method= RequestMethod.POST, produces= MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> generateBranchedToken(@RequestBody Long branchRid,
        HttpServletRequest request) {
        SecUser user= userService.findById(SecurityUtil.getCurrentUser().getRid());
        if (user.getBranchId() != null) {
            throw new BusinessException("Can't set branch rid in a user belongs to a branch",
                "userHasBranch", ErrorSeverity.ERROR);
        }
        Map<String, Object> claims= TokenUtil.decodeToken(SecurityUtil.getToken());
        Map<String, Object> userFromToken= (Map<String, Object>) claims.get("user");
        userFromToken.put("branchId", branchRid);
        claims.put("user", userFromToken);
        String token= TokenUtil.encodeToken(claims);
        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

}
