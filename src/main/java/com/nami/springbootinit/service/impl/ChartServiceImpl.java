package com.nami.springbootinit.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nami.springbootinit.mapper.ChartMapper;
import com.nami.springbootinit.model.entity.Chart;
import com.nami.springbootinit.service.ChartService;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

    @Override
    public List<Chart> getChartListByUserId(Long userId) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper();
        queryWrapper.eq("userId", userId);
        List<Chart> list = this.list(queryWrapper);
        return list;
    }

    public boolean isJsonString(String unKnowString) {
        if (unKnowString == null || unKnowString == "") {
            return false;
        }

        try {
            JSON.parse(unKnowString);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}




