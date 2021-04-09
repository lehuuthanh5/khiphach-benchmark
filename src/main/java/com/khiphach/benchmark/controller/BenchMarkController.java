package com.khiphach.benchmark.controller;

import com.khiphach.benchmark.model.Result;
import com.khiphach.benchmark.service.BenchMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class BenchMarkController {
    @Autowired
    private BenchMarkService benchMarkService;


    @GetMapping("/get")
    public ResponseEntity<Result> getBenchMark(@RequestParam(required = false) String gpu,
                                               @RequestParam(required = false) String cpu) {
        return ResponseEntity.ok(benchMarkService.getBenchMark(cpu, gpu));
    }
}
