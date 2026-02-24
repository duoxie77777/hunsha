package com.hunsha.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hunsha.entity.ChatMessage;
import com.hunsha.mapper.ChatMessageMapper;
import com.hunsha.service.ChatMessageService;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
}
