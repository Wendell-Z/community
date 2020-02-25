package com.nowcoder.community.controller;

import com.nowcoder.community.annontation.LoginRequired;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.holder.UserHolder;
import com.nowcoder.community.mapper.MessageMapper;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserHolder userHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 获取当前用户的会话列表
     *
     * @param model
     * @param page
     * @return
     */
    @LoginRequired
    @GetMapping(value = "/letter/list")
    public String getMessageList(Model model, Page page) {
        //判断是否登录 注解
        User user = userHolder.getUser();
        //设置分页
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //获取当前用户会话列表
        List<Message> messageList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> list = new ArrayList<>();
        //获取未读数
        if (messageList != null) {
            for (Message message : messageList) {
                Map<String, Object> map = new HashMap<>();
                //放入最新的message
                map.put("conversation", message);
                //放入未读私信数
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                //放入会话对方
                User target = user.getId() == message.getFromId() ? userService.findUserById(message.getToId()) : userService.findUserById(message.getFromId());
                map.put("target", target);
                //放入私信总数
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                list.add(map);
            }
        }
        model.addAttribute("conversations", list);
        //总的会话未读数
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        return "/site/letter";
    }

    /**
     * 获取某会话的所有私信
     *
     * @param conversationId
     * @param model
     * @param page
     * @return
     */
    @LoginRequired
    @GetMapping(value = "/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable(value = "conversationId") String conversationId, Model model, Page page) {
        int letterCount = messageService.findLetterCount(conversationId);
        page.setRows(letterCount);
        page.setPath("/letter/detail/" + conversationId);
        page.setLimit(5);
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> list = new ArrayList<>();
        if (letterList != null) {
            for (Message letter : letterList) {
                Map<String, Object> map = new HashMap<>();
                //放入当前letter
                map.put("letter", letter);
                //放入fromUser
                User from = userService.findUserById(letter.getFromId());
                map.put("fromUser", from);
                //放入toUser  不需要 只显示谁发的
//                User to = userService.findUserById(letter.getToId());
//                map.put("to",to);
                list.add(map);
            }
        }
        model.addAttribute("letters", list);
        // 私信目标 因为前端页面有个标题要显示
        model.addAttribute("target", getLetterTarget(conversationId));
        //设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    /**
     * 获取当前会话的对方用户
     *
     * @param conversationId
     * @return
     */
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (userHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    @PostMapping(value = "/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {
        //查用户
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getString(403, "目标用户不存在!");
        }
        //判断自己
        if (target.getId() == userHolder.getUser().getId()) {
            return CommunityUtil.getString(403, "不能给自己发私信！");
        }
        //私信内容不能为空
        if (null == content || StringUtils.isBlank(content)) {
            return CommunityUtil.getString(403, "私信内容不能为空！");
        }

        Message message = new Message();
        message.setFromId(userHolder.getUser().getId());
        message.setToId(target.getId());
        //为什么不能用三元表达式
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }

        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getString(200, "发送成功！");

    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (userHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    //删除私信 若当前用户能删除对方的和自己的 那么双方都会看不到
    @PostMapping(value = "/letter/delete")
    @ResponseBody
    public String deleteLetter(String letterId) {
        System.out.println(letterId);
        return CommunityUtil.getString(200);
    }
}
