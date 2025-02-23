package com.matchmaker.controller;

import com.matchmaker.common.dto.MatchInfoAddRequest;
import com.matchmaker.common.dto.MatchInfoAddResponse;
import com.matchmaker.common.dto.FindMatchResponse;
import com.matchmaker.common.dto.FindMatchRequest;
import com.matchmaker.constants.GlobalConstants;
import com.matchmaker.constants.RequestURI;
import com.matchmaker.service.MatchInfoService;
import com.matchmaker.service.MatchService;
import com.matchmaker.service.impl.MatchServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class MatchController {

    Logger logger = LogManager.getLogger(MatchController.class);

    @Autowired
    MatchService matchService;

    @Autowired
    MatchInfoService matchInfoService;

    @RequestMapping(method = RequestMethod.GET, value = RequestURI.FIND_MATCH_API)
    @ResponseBody
    ResponseEntity<FindMatchResponse> findMatch(@Valid @RequestBody FindMatchRequest request) throws Exception {
        logger.info("request is" + GlobalConstants.objectMapper.writeValueAsString(request));
        FindMatchResponse response = matchService.findMatchForUser(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = RequestURI.CREATE_MATCH_API)
    @ResponseBody
    ResponseEntity<MatchInfoAddResponse> findMatch(@RequestBody MatchInfoAddRequest request) throws Exception {
        MatchInfoAddResponse response = matchInfoService.setMatchInfo(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
