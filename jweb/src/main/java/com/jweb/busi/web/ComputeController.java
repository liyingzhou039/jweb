package com.jweb.busi.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("compute")
public class ComputeController {
    @RequestMapping("/flavor-list")
    public String flavorList(){
        return "busi/compute/flavor-list";
    }
    @RequestMapping("/image-list")
    public String imageList(){
        return "busi/compute/image-list";
    }
    @RequestMapping("/server-list")
    public String serverList(){
        return "busi/compute/server-list";
    }
}
