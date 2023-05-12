package com.zw.domain;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2023-02-07
 */
@Data
public class RcSu  {
    private Long id;

    /**
     * 监控主机名称
     */
    private String suName;

    /**
     * 监控主机编码
     */
    private String suCode;

    /**
     * 监控主机IP
     */
    private String suIp;

    /**
     * 监控主机端口号
     */
    private Integer suPort;

    @Override
    public String toString() {
        return "RcSu{" +
        "id=" + id +
        ", suName=" + suName +
        ", suCode=" + suCode +
        ", suIp=" + suIp +
        ", suPort=" + suPort +
        "}";
    }
}
