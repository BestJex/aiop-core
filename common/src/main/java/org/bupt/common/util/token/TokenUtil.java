package org.bupt.common.util.token;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * JWT生成与验证token
 * Created by ken on 2017/6/8.
 */
public class TokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    //Sample method to construct a JWT
    public static String createToken(Identity identity, String apiKeySecret) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiKeySecret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(String.valueOf(identity.getId()))
                .setIssuedAt(now)
                .setSubject(identity.getId() + "/" + identity.getClientId())
                .setIssuer(identity.getIssuer())
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        long ttlMillis = identity.getDuration();
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
            identity.setDuration(exp.getTime());
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static Identity parseToken(String token, String apiKeySecret) throws SignatureException {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(apiKeySecret))
                .parseClaimsJws(token).getBody();

        String[] subjectInfos = claims.getSubject().split("/");
        String id = subjectInfos[0];
        String clientId = subjectInfos[1];

        // 封装成pojo
        Identity identity = new Identity();
        identity.setId(Integer.parseInt(id));
        identity.setClientId(clientId);
        identity.setDuration(claims.getExpiration().getTime());

        return identity;
    }
}
