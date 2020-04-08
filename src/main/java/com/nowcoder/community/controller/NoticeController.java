package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.annontation.LoginRequired;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.holder.UserHolder;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.common.protocol.types.Field;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NoticeController implements CommunityConstant {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserHolder userHolder;
    @Autowired
    private UserService userService;

    @LoginRequired
    @GetMapping(value = "/notice/list")
    public String getNoticeList(Model model) {
        User user = userHolder.getUser();
        Map<String, Object> commentVO = getTopicVO(user.getId(), TOPIC_COMMENT);
        if (commentVO != null) {
            //System.out.println(JSONObject.toJSONString(commentVO));
            model.addAttribute("commentNotice", commentVO);
        }

        Map<String, Object> likeVO = getTopicVO(user.getId(), TOPIC_LIKE);
        if (likeVO != null) {
            model.addAttribute("likeNotice", likeVO);
        }

        Map<String, Object> followVO = getTopicVO(user.getId(), TOPIC_FOLLOW);
        if (followVO != null) {
            model.addAttribute("followNotice", followVO);
        }


        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";


    }

    private HashMap<String, Object> getTopicVO(int userId, String topic) {
        Message message = messageService.findLatestNotice(userId, topic);

        if (message != null) {
            HashMap<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(userId, topic);
            messageVO.put("count", count);

            int unread = messageService.findNoticeUnreadCount(userId, topic);
            messageVO.put("unread", unread);
            return messageVO;
        } else {
            return null;
        }

    }

    @LoginRequired
    @GetMapping(value = "/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable(value = "topic") String topic, Model model, Page page) {
        User user = userHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                // 通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);

        // 设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
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
}
