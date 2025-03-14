package com.matchmaker.dao;


import com.matchmaker.common.db.dao.CommonDao;
import com.matchmaker.common.db.service.CommonDbService;
import com.matchmaker.constants.GlobalConstants;
import com.matchmaker.model.MatchInfo;
import com.matchmaker.util.DateConvertUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

@Repository
public class MatchInfoDao {

    @Autowired
    CommonDbService commonDbService;

    @Autowired
    CommonDao commonDao;

    Logger logger = LogManager.getLogger(MatchInfoDao.class);

    public MatchInfo getMatchInfoFromMatchId(String matchId) throws Exception {
        String query = "select m from MatchInfo m where m.matchId = :match_id";
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("match_id", matchId);
        return commonDbService.selectEntityByCriteria(MatchInfo.class, query, queryMap);
    }

    public int getUserMatchCountForDay(String userId) throws Exception {
        Date currDate = new Date();
        Date startDate = DateConvertUtils.getStartOfDate(currDate);
        Date endDate = DateConvertUtils.getEndOfDate(currDate);
        return getUserMatchCountInDate(userId, startDate, endDate);
    }

    public int getUserMatchCountInDate(String userId, Date startDate, Date endDate) throws Exception {
        String query = "select um.user_id,count(*) from match_info m left join user_match_mapping um on m.id = um.match_info_id" +
                " where um.user_id = :user_id and m.created_at >= :start_date and m.created_at <= :end_date";
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("user_id", userId);
        queryMap.put("start_date", startDate);
        queryMap.put("end_date", endDate);

        List<Object[]> result = commonDao.findByQuery(query, queryMap, 0, 0);
        logger.info(GlobalConstants.objectMapper.writeValueAsString(result));
        if (result.isEmpty() || result.get(0) == null) {
            return 0;
        }
        return Integer.parseInt(String.valueOf(result.get(0)[1]));
    }
}
