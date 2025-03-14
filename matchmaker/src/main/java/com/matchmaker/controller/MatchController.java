package com.matchmaker.controller;

import com.matchmaker.common.dto.MatchInfoAddRequest;
import com.matchmaker.common.dto.MatchInfoAddResponse;
import com.matchmaker.common.dto.FindMatchResponse;
import com.matchmaker.common.dto.FindMatchRequest;
import com.matchmaker.constants.RequestURI;
import com.matchmaker.service.MatchInfoService;
import com.matchmaker.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class MatchController {

    @Autowired
    MatchService matchService;

    @Autowired
    MatchInfoService matchInfoService;

    @RequestMapping(method = RequestMethod.GET, value = RequestURI.FIND_MATCH_API)
    @ResponseBody
    ResponseEntity<FindMatchResponse> findMatch(@Valid @RequestBody FindMatchRequest request) throws Exception {
        FindMatchResponse response = matchService.findMatchForUser(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = RequestURI.SET_MATCH_INFO_API)
    @ResponseBody
    ResponseEntity<MatchInfoAddResponse> setMatchInfo(@RequestBody MatchInfoAddRequest request) throws Exception {
        MatchInfoAddResponse response = matchInfoService.setMatchInfo(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
