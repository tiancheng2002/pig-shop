package com.zhu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2022-06-20
 */
@TableName("t_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "rid",type = IdType.AUTO)
    private Integer rid;

    private String username;

    private String password;

    private String salt;

    private Integer role;
    
}
