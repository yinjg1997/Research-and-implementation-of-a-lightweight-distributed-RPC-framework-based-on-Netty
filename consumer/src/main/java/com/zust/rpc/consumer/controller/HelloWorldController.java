package com.zust.rpc.consumer.controller;

import com.zust.rpc.api.service.HelloWordService;
import com.zust.rpc.client.annotation.RpcAutowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HelloWorldController {


    @RpcAutowired(version = "1.0")
    private HelloWordService helloWordService;

    @GetMapping("/hello/world")
    public ResponseEntity<String> pullServiceInfo(@RequestParam("name") String name){
        return  ResponseEntity.ok(helloWordService.sayHello(name));
    }


}
