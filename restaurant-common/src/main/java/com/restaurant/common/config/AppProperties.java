package com.restaurant.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用配置属性
 */
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Upload upload = new Upload();

    public static class Jwt {
        private String secret = "restaurant-ordering-system-jwt-secret-key-2023";
        private long expiration = 86400; // 24小时

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }
    }

    public static class Upload {
        private String path = "/tmp/restaurant/uploads";
        private String maxFileSize = "10MB";
        private String allowedTypes = "jpg,jpeg,png,gif";

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMaxFileSize() {
            return maxFileSize;
        }

        public void setMaxFileSize(String maxFileSize) {
            this.maxFileSize = maxFileSize;
        }

        public String getAllowedTypes() {
            return allowedTypes;
        }

        public void setAllowedTypes(String allowedTypes) {
            this.allowedTypes = allowedTypes;
        }
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }
}