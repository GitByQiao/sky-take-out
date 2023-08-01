package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("select *from user where openid=#{openid}")
    User getByOpenId(String openid);

    void insert(User user);

    /**
     * 统计每天的新增用户数量
     *
     * @param minTime
     * @param maxTime
     * @return
     */
    Long getReportUserStatistics(LocalDateTime minTime, LocalDateTime maxTime);

    /**
     * 统计每天的新增用户总量
     *
     * @param maxTime
     * @return
     */
    Long getReportCountStatistics(LocalDateTime maxTime);

    /**
     * 根据动态条件统计用户数量
     *
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
