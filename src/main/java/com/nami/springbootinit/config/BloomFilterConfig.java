package com.nami.springbootinit.config;

import org.springframework.stereotype.Component;

/**
 * @author nami404
 * * @date 2025/3/19 09:44
 */
@Component
public class BloomFilterConfig {
    private Long expectedInsertions = 1000L;

    private Double fpp = 0.01D;

    public Long getExpectedInsertions() {
        return expectedInsertions;
    }

    public void setExpectedInsertions(Long expectedInsertions) {
        this.expectedInsertions = expectedInsertions;
    }

    public Double getFpp() {
        return fpp;
    }

    public void setFpp(Double fpp) {
        this.fpp = fpp;
    }


}
