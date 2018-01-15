package org.bupt.aiop.mis.mapper;

import org.bupt.aiop.mis.pojo.po.AbilityInvokeLog;
import org.bupt.aiop.mis.pojo.vo.AbilityInvokeLogStatistic;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface AbilityInvokeLogMapper extends Mapper<AbilityInvokeLog> {
	List<AbilityInvokeLogStatistic> selectStatistic(Map<String, Object> filters);
}