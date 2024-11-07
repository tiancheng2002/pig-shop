package com.zhu.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-02
 */
@TableName("t_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uid")
    private Long uid;

    private String nickname;

    private String password;

    private String salt;

    private String head;

    private char sex;

    @TableField(fill = FieldFill.INSERT)
    private Date registerDate;

}
