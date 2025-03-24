package com.nami.springbootinit.proxytest2;

import java.util.UUID;

public class ApiHeader {
    public enum ClientType {
        OpenFeign("openfeign"),
        RestTemplate("rest_template"),
        WebClient("web_client");

        private final String typename;

        ClientType(String typename) {
            this.typename = typename;
        }

        @Override
        public String toString() {
            return typename;
        }
    }

    private String id;
    private ClientType clientType;

    public ApiHeader(ClientType clientType) {
        this.id = generateId();
        this.clientType = clientType;
    }

    public String getId() {
        return id;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
