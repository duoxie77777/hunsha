package com.hunsha.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hunsha.common.Result;
import com.hunsha.entity.ChatMessage;
import com.hunsha.entity.User;
import com.hunsha.service.ChatMessageService;
import com.hunsha.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员聊天接口
 */
@Tag(name = "后台-消息管理")
@RestController
@RequestMapping("/api/admin/chat")
@RequiredArgsConstructor
public class AdminChatController {

    private final ChatMessageService chatMessageService;
    private final UserService userService;

    @Operation(summary = "获取会话列表（有过聊天的用户列表）")
    @GetMapping("/sessions")
    public Result<List<Map<String, Object>>> getSessions() {
        // 查找所有与管理员(receiverId=0)有过消息往来的用户ID
        List<ChatMessage> allMessages = chatMessageService.list(
                new LambdaQueryWrapper<ChatMessage>()
                        .and(w -> w.eq(ChatMessage::getReceiverId, 0L))
                        .or(w -> w.eq(ChatMessage::getSenderId, 0L))
                        .orderByDesc(ChatMessage::getCreatedAt)
        );

        // 按用户ID分组
        Map<Long, List<ChatMessage>> grouped = new LinkedHashMap<>();
        for (ChatMessage msg : allMessages) {
            Long userId = msg.getSenderId() == 0L ? msg.getReceiverId() : msg.getSenderId();
            grouped.computeIfAbsent(userId, k -> new ArrayList<>()).add(msg);
        }

        // 查询用户信息
        Set<Long> userIds = grouped.keySet();
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            for (User u : users) {
                userMap.put(u.getId(), u);
            }
        }

        // 构建会话列表
        List<Map<String, Object>> sessions = new ArrayList<>();
        for (Map.Entry<Long, List<ChatMessage>> entry : grouped.entrySet()) {
            Long userId = entry.getKey();
            List<ChatMessage> msgs = entry.getValue();
            User user = userMap.get(userId);

            ChatMessage lastMsg = msgs.get(0); // 已按 createdAt desc 排序
            long unread = msgs.stream()
                    .filter(m -> m.getSenderId().equals(userId) && m.getIsRead() == 0)
                    .count();

            Map<String, Object> session = new HashMap<>();
            session.put("userId", userId);
            session.put("nickname", user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "用户" + userId);
            session.put("avatar", user != null ? user.getAvatar() : null);
            session.put("phone", user != null ? user.getPhone() : "");
            session.put("lastMessage", lastMsg.getContent());
            session.put("lastMessageType", lastMsg.getType());
            session.put("lastTime", lastMsg.getCreatedAt());
            session.put("unreadCount", unread);
            sessions.add(session);
        }

        // 按未读优先、最后消息时间倒序排列
        sessions.sort((a, b) -> {
            long ua = (long) a.get("unreadCount"), ub = (long) b.get("unreadCount");
            if (ua != ub) return Long.compare(ub, ua);
            return ((Comparable) b.get("lastTime")).compareTo(a.get("lastTime"));
        });

        return Result.ok(sessions);
    }

    @Operation(summary = "获取与某用户的聊天记录")
    @GetMapping("/messages/{userId}")
    public Result<List<ChatMessage>> getMessages(@PathVariable Long userId) {
        List<ChatMessage> messages = chatMessageService.list(
                new LambdaQueryWrapper<ChatMessage>()
                        .and(w -> w.eq(ChatMessage::getSenderId, userId).eq(ChatMessage::getReceiverId, 0L))
                        .or(w -> w.eq(ChatMessage::getSenderId, 0L).eq(ChatMessage::getReceiverId, userId))
                        .orderByAsc(ChatMessage::getCreatedAt)
        );
        // 标记用户发给管理员的消息为已读
        chatMessageService.update(
                new LambdaUpdateWrapper<ChatMessage>()
                        .eq(ChatMessage::getSenderId, userId)
                        .eq(ChatMessage::getReceiverId, 0L)
                        .eq(ChatMessage::getIsRead, 0)
                        .set(ChatMessage::getIsRead, 1)
        );
        return Result.ok(messages);
    }

    @Operation(summary = "管理员发送消息给用户")
    @PostMapping("/send/{userId}")
    public Result<ChatMessage> send(@PathVariable Long userId, @RequestBody ChatMessage msg) {
        msg.setSenderId(0L); // 0 代表管理员
        msg.setReceiverId(userId);
        if (msg.getType() == null || msg.getType().isEmpty()) {
            msg.setType("text");
        }
        msg.setIsRead(0);
        chatMessageService.save(msg);
        return Result.ok(msg);
    }

    @Operation(summary = "获取管理员总未读消息数")
    @GetMapping("/unread")
    public Result<Long> totalUnread() {
        long count = chatMessageService.count(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getReceiverId, 0L)
                        .eq(ChatMessage::getIsRead, 0)
        );
        return Result.ok(count);
    }
}
