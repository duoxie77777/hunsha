package com.hunsha.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hunsha.common.Result;
import com.hunsha.entity.ChatMessage;
import com.hunsha.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端聊天接口（senderId=当前用户, receiverId=0 表示客服）
 */
@Tag(name = "聊天接口")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    @Operation(summary = "发送消息给客服")
    @PostMapping("/send")
    public Result<ChatMessage> send(@RequestBody ChatMessage msg, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        msg.setSenderId(userId);
        msg.setReceiverId(0L); // 0 代表客服/管理员
        if (msg.getType() == null || msg.getType().isEmpty()) {
            msg.setType("text");
        }
        msg.setIsRead(0);
        chatMessageService.save(msg);
        return Result.ok(msg);
    }

    @Operation(summary = "获取当前用户与客服的聊天记录")
    @GetMapping("/messages")
    public Result<List<ChatMessage>> getMessages(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ChatMessage> messages = chatMessageService.list(
                new LambdaQueryWrapper<ChatMessage>()
                        .and(w -> w
                                .eq(ChatMessage::getSenderId, userId).eq(ChatMessage::getReceiverId, 0L)
                        )
                        .or(w -> w
                                .eq(ChatMessage::getSenderId, 0L).eq(ChatMessage::getReceiverId, userId)
                        )
                        .orderByAsc(ChatMessage::getCreatedAt)
        );
        // 标记客服发给用户的消息为已读
        chatMessageService.update(
                new LambdaUpdateWrapper<ChatMessage>()
                        .eq(ChatMessage::getSenderId, 0L)
                        .eq(ChatMessage::getReceiverId, userId)
                        .eq(ChatMessage::getIsRead, 0)
                        .set(ChatMessage::getIsRead, 1)
        );
        return Result.ok(messages);
    }

    @Operation(summary = "获取用户未读消息数")
    @GetMapping("/unread")
    public Result<Long> unreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        long count = chatMessageService.count(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getSenderId, 0L)
                        .eq(ChatMessage::getReceiverId, userId)
                        .eq(ChatMessage::getIsRead, 0)
        );
        return Result.ok(count);
    }
}
