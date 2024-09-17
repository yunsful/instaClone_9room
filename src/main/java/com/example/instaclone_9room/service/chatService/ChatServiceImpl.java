package com.example.instaclone_9room.service.chatService;


import com.example.instaclone_9room.apiPayload.code.status.ErrorStatus;
import com.example.instaclone_9room.apiPayload.exception.handler.ChatCategoryHandler;
import com.example.instaclone_9room.apiPayload.exception.handler.MemberCategoryHandler;
import com.example.instaclone_9room.controller.dto.ChatDTO;

import com.example.instaclone_9room.converter.ChatConverter;
import com.example.instaclone_9room.domain.DM.ChatPart;
import com.example.instaclone_9room.domain.DM.ChatRoom;
import com.example.instaclone_9room.domain.DM.Message;
import com.example.instaclone_9room.domain.userEntity.UserEntity;
import com.example.instaclone_9room.repository.DmRepository.ChatPartRepository;


import com.example.instaclone_9room.repository.DmRepository.ChatRoomRepository;
import com.example.instaclone_9room.repository.DmRepository.MessageRepository;
import com.example.instaclone_9room.repository.userEntityRepository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatPartRepository chatPartRepository;
    private final MessageRepository messageRepository;


    @Override
    public ChatDTO.ChatRoomCreateResp chatRoomCreate(ChatDTO.ChatRoomCreateDTO request, String userName) {

        ChatRoom chatRoom = ChatRoom.builder().build();
        chatRoomRepository.save(chatRoom);

        List<String> allUsers = new ArrayList<>(request.getInvitedUserNames());
        allUsers.add(userName);

        List<ChatPart> chatParts = new ArrayList<>();

        for (String user : allUsers) {
            UserEntity userEntity = findUser(user);

            List<String> otherUsers = new ArrayList<>(allUsers);
            otherUsers.remove(user);

            String chatRoomName = String.join(", ", otherUsers);

            ChatPart chatPart = ChatPart.builder()
                    .userEntity(userEntity)
                    .chatRoom(chatRoom)
                    .chatRoomName(chatRoomName)
                    .build();

            chatParts.add(chatPart);
        }

        chatPartRepository.saveAll(chatParts);

        return new ChatDTO.ChatRoomCreateResp(chatRoom.getId());

    }



    @Override
    public void updateChatRoomName(ChatDTO.ChatRoomNameUpdateDTO request, Long chatRoomId, String userName) {

        UserEntity findUser = findUser(userName);
        List<ChatPart> chatParts = chatPartRepository.findAllByChatRoomId(chatRoomId);

        boolean isAuthorized = chatParts.stream()
                .anyMatch(chatPart -> chatPart.getUserEntity().getId().equals(findUser.getId()));

        if (!isAuthorized) {
            throw new ChatCategoryHandler(ErrorStatus.UNAUTHORIZED_ACCESS);
        } else {
            chatParts.forEach(chatPart -> chatPart.update(request.getChatRoomName()));
        }

        chatPartRepository.saveAll(chatParts);

    }

    // QueryDSL 공부 후 해볼 것
    @Override
    public ChatDTO.ChatRoomListResponse getChatRoomSummaryList(String userName) {


        return null;
    }



    // -----------------------소켓 통신 사용 예정----------------------- //

    @Override
    public void saveMessage(ChatDTO.MessageDTO messageDTO) {
        // 로그 추가: chatRoomId와 senderId 확인
        log.info("Saving message with chatRoomId: {}, senderId: {}", messageDTO.getChatRoomId(), messageDTO.getSenderId());

        ChatPart chatPart = chatPartRepository.findChatPartByChatRoomIdAndUserEntityId(messageDTO.getChatRoomId(),
                messageDTO.getSenderId()).orElseThrow(() -> new RuntimeException("채팅 설정 오류"));

        Message message = ChatConverter.toMessage(messageDTO, chatPart);

        messageRepository.save(message);
    }

//    @Override
//    public void startChat(Long chatRoomId, WebSocketSession session) {
//        // 채팅방을 찾기
//        ChatRoom chatRoom = findChatRoom(chatRoomId);
//        Set<WebSocketSession> chatRoomSessions = webSocketChatHandler.getChatRoomSessions(chatRoomId);
//
//        if (chatRoomSessions == null) {
//            chatRoomSessions = new HashSet<>();
//            webSocketChatHandler.addChatRoomSessions(chatRoomId, chatRoomSessions);
//        }
//
//        // 새로운 세션을 추가합니다.
//        chatRoomSessions.add(session);
//
//        // 입장 메시지를 전송합니다.
//        ChatDTO.MessageDTO enterMessage = ChatDTO.MessageDTO.builder()
//                .messageType(ChatDTO.MessageDTO.MessageType.ENTER)
//                .chatRoomId(chatRoomId)
//                .message("사용자가 채팅방에 입장했습니다.")
//                .build();
//
//        webSocketChatHandler.sendMessageToChatRoom(enterMessage,chatRoomSessions);
//    }
//
//    @Override
//    public void sendMessage(ChatDTO.MessageDTO messageDTO) {
//        Long chatRoomId = messageDTO.getChatRoomId();
//        Set<WebSocketSession> chatRoomSessions = webSocketChatHandler.getChatRoomSessions(chatRoomId);
//
//        if (chatRoomSessions == null) {
//            // 채팅방 세션이 없으면 로그를 남기고 리턴
//            log.warn("채팅방 세션이 없습니다: chatRoomId={}", chatRoomId);
//            return;
//        }
//
//        // 메시지를 채팅방 세션으로 전송
//        webSocketChatHandler.sendMessageToChatRoom(messageDTO, chatRoomSessions);
//
//    }

    @Override
    public void chatRoomDelete(Long chatRoomId, String userName) {

    }


    private UserEntity findUser(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(
                () -> new MemberCategoryHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private ChatRoom findChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new ChatCategoryHandler(ErrorStatus.CHATROOM_NOT_FOUND));
    }

    private ChatPart findChatPartByRoom(Long chatRoomId) {
        return chatPartRepository.findByChatRoomId(chatRoomId).orElseThrow(
                () -> new ChatCategoryHandler(ErrorStatus.CHATPART_NOT_FOUND)
        );
    }


    private ChatPart findChatPartByUser(Long userId) {
        return chatPartRepository.findByUserEntityId(userId
        ).orElseThrow(
                () -> new ChatCategoryHandler(ErrorStatus.CHATPART_NOT_FOUND)
        );
    }

}
