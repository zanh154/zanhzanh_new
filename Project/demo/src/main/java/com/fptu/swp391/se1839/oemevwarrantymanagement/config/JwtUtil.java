package com.fptu.swp391.se1839.oemevwarrantymanagement.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import java.text.ParseException;

public class JwtUtil {

    private static final String SIGN_KEY = "your-secret-key"; // phải trùng với key tạo token

    public static JwtInfo parseToken(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            var claims = jwsObject.getPayload().toJSONObject();

            Long userId = claims.get("userId") != null ? Long.parseLong(claims.get("userId").toString()) : null;
            String role = claims.get("role") != null ? claims.get("role").toString() : null;
            Long serviceCenterId = claims.get("serviceCenterId") != null
                    ? Long.parseLong(claims.get("serviceCenterId").toString())
                    : null;

            return new JwtInfo(userId, role, serviceCenterId);

        } catch (ParseException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}
