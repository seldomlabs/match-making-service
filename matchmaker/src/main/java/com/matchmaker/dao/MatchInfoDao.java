package com.matchmaker.dao;


import com.matchmaker.common.db.dao.CommonDao;
import com.matchmaker.common.db.service.CommonDbService;
import com.matchmaker.common.dto.MatchDto;
import com.matchmaker.model.MatchInfo;
import com.matchmaker.util.DateConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    public MatchInfo getMatchInfoFromRequestId(String requestId) throws Exception {
        String query = "select m from MatchInfo m where m.requestId = :request_id";
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("request_id", requestId);
        return commonDbService.selectEntityByCriteria(MatchInfo.class, query, queryMap);
    }

    public MatchDto getUsersForMatch(String requestId) throws Exception {
        String query = "select um.user_id, m.id from match_info m left join user_match_mapping um on m.id = um.match_id" +
                " where m.request_id = :request_id";
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("request_id", requestId);
        List<Object[]> result = commonDao.findByQuery(query, queryMap, 0, 0);
        if (result.isEmpty()) {
            return null;
        }
        MatchDto matchDto = new MatchDto();
        List<String> userList = new ArrayList<>();
        matchDto.setMatchId(Long.valueOf(String.valueOf(result.get(0)[1])));
        for (Object[] obj : result) {
            userList.add(String.valueOf(obj[0]));
        }
        matchDto.setUsers(userList);
        return matchDto;
    }

    public int getUserMatchCountForDay(String userId) throws Exception {
        Date currDate = new Date();
        Date startDate = DateConvertUtils.getStartOfDate(currDate);
        Date endDate = DateConvertUtils.getEndOfDate(currDate);
        return getUserMatchCountInDate(userId, startDate, endDate);
    }

    public int getUserMatchCountInDate(String userId, Date startDate, Date endDate) throws Exception {
        String query = "select m.id, count(*) from match_info m left join user_match_mapping um on m.id = um.match_id" +
                " where um.user_id = :user_id and m.created_at >= :start_date and m.created_at <= end_date";
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("user_id", userId);
        queryMap.put("start_date", startDate);
        queryMap.put("end_date", endDate);

        List<Object[]> result = commonDao.findByQuery(query, queryMap, 0, 0);
        if (result.isEmpty() || result.get(0) == null) {
            return 0;
        }
        return Integer.parseInt(String.valueOf(result.get(0)[1]));
    }
}
