package com.zw.controller.msg;


import com.zw.common.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 心跳相关
 *
 * @author kingsmartsi
 */
@RestController
@RequestMapping("/heartbeat")
@Slf4j
public class HeartbeatController {
    @Value("${server.port}")
    private int port;
    @GetMapping("/iotService")
    public AjaxResult iotService()  {
        return AjaxResult.success();
    }


}
