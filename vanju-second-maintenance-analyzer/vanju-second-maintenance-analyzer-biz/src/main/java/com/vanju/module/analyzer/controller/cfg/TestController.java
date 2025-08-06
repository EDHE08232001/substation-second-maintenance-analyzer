package com.vanju.module.analyzer.controller.cfg;

import com.vanju.framework.common.pojo.CommonResult;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.xmlbeans.impl.tool.CommandLine;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 二次检修仪测试 Controller
 */
@Slf4j
@RestController
@RequestMapping("/test/api")
public class TestController {
    @GetMapping("/hello")
    public CommonResult<String> getPageList() {
        return CommonResult.success("okok");
    }

    @PostConstruct
    public void init() {
        log.info("okokokoko=======");
    }

    @GetMapping("/run-script")
    public static List<String> listLibDirectoryWithPwd() throws Exception {
        List<String> result = new ArrayList<>();

//        ProcessBuilder pwdPb = new ProcessBuilder("pwd");
//        pwdPb.directory(new java.io.File("/home/debian/project/install_env/data/jxyServer/bin"));
//        pwdPb.redirectErrorStream(true);
//        Process pwdProcess = pwdPb.start();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(pwdProcess.getInputStream()))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                result.add(line);
//            }
//        }
//        pwdProcess.waitFor();

        ProcessBuilder lsPb = new ProcessBuilder("./scdparse", "/home/debian/project/install_env/data/jxyServer/scdFile/devScd.scd");
        lsPb.directory(new java.io.File("/home/debian/project/install_env/data/jxyServer/bin"));
        lsPb.redirectErrorStream(true);
        Process lsProcess = lsPb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(lsProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        }
        lsProcess.waitFor();

        return result;
    }
}