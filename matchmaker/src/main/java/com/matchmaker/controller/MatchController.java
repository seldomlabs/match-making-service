package com.matchmaker.controller;

import com.matchmaker.common.dto.*;
import com.matchmaker.constants.RequestURI;
import com.matchmaker.service.MatchInfoService;
import com.matchmaker.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    ResponseEntity<MatchInfoAddResponse> setMatchInfo(@Valid @RequestBody MatchInfoAddRequest request) throws Exception {
        MatchInfoAddResponse response = matchInfoService.setMatchInfo(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = RequestURI.UPDATE_MATCH_INFO_API)
    @ResponseBody
    ResponseEntity<MatchInfoAddResponse> updateMatchInfo(@Valid @RequestBody UpdateMatchInfoRequest request) throws Exception {
        MatchInfoAddResponse response = matchInfoService.updateMatchInfo(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = RequestURI.GET_USER_MATCH_INFO_API)
    @ResponseBody
    ResponseEntity<UserMatchInfoResponse> getUserMatchInfo(@RequestParam String userId) throws Exception {
        UserMatchInfoResponse response = matchInfoService.getUserMatchInfo(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
