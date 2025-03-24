package com.nami.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nami.springbootinit.model.entity.Chart;

import java.util.List;

/**
 *
 */
public interface ChartService extends IService<Chart> {

    List<Chart> getChartListByUserId(Long userId);

    boolean isJsonString(String unKnowString);

}
