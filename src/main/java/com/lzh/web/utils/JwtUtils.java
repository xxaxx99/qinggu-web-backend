package com.lzh.web.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lzh.web.model.entity.User;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Jwt工具类
 *
 * @author lzh
 */
@Transactional(rollbackFor = Exception.class)
@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtUtils implements InitializingBean {

    @Value("${jwt.secret_key}")
    private String secret_key;

    @Value("${jwt.exp_time}")
    private int exp_time;

    //定义公共静态常量
    public static String SECRET_KEY;
    public static Integer EXP_TIME;

    @Override
    public void afterPropertiesSet() {
        SECRET_KEY = secret_key;
        EXP_TIME = exp_time;
    }

    public static String createJwt(User loginUser) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withIssuer("auth0")
                .withClaim("id", loginUser.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * EXP_TIME))
                .sign(algorithm);
    }



}
