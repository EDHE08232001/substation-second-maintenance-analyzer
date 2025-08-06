package com.vanju.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 * @author 万炬源码
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${vanju.info.base-package}
@SpringBootApplication(scanBasePackages = {"${vanju.info.base-package}.server", "${vanju.info.base-package}.module"})
public class SecondMaintenanceAnalyzerApp {
    public static void main(String[] args) {
        SpringApplication.run(SecondMaintenanceAnalyzerApp.class, args);
    }
}