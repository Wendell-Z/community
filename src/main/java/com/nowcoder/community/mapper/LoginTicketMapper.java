package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated
public interface LoginTicketMapper {


    /**
     * 设置ticket 设置过期时间 userid ticket 成功后返回ticket
     *
     * @param loginTicket
     * @return
     */
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);


    /**
     * 更改过期状态 成功后返回int
     *
     * @param ticket
     * @param status
     * @return
     */
    @Update("update login_ticket set status = #{status} where ticket = #{ticket}")
    int updateStatus(String ticket, int status);

    /**
     * 获取ticket
     *
     * @param ticket
     * @return
     */
    @Select("select * from login_ticket where ticket = #{ticket}")
    LoginTicket getLoginTicket(String ticket);
}
