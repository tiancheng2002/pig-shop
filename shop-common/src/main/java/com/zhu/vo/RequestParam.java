package com.zhu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestParam {

    private String key;
    private Integer tid;
    private Integer tpid;
    private String sort;

}
