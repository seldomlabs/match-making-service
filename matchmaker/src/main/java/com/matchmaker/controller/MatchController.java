package com.matchmaker.controller;

import com.matchmaker.common.dto.FindMatchResponse;
import com.matchmaker.common.dto.FindMatchRequest;
import com.matchmaker.constants.RequestURI;
import com.matchmaker.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MatchController {

    @Autowired
    MatchService matchService;

    @RequestMapping(method = RequestMethod.GET, value = RequestURI.FIND_MATCH_API)
    @ResponseBody
    ResponseEntity<FindMatchResponse> findMatch(@RequestBody FindMatchRequest request) throws Exception {
        FindMatchResponse response = matchService.findMatchForUser(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
