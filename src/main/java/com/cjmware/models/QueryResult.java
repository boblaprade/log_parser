package com.cjmware.models;

import lombok.Data;

@Data
public class QueryResult {

    private String ip_address;
    public Long hits;
}
