package com.example.chat.controller;

import com.example.chat.constants.WebSocketDestinations;
import com.example.chat.dto.RoomDTO;
import com.example.chat.service.RoomEntityService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
public class RoomsController {
    private final RoomEntityService roomService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/fetchallrooms")
    public List<RoomDTO> fetchAll(Model model, Principal principal) {
        return roomService.findRoomsByUser(principal.getName());
    }

    @GetMapping("/writetofound")
    public RoomDTO writeToFoundUser(Model model, @RequestParam Long principalId, @RequestParam Long recipientId) {
        simpMessagingTemplate.convertAndSend(
                WebSocketDestinations.NEW_DIALOG_TOPIC + recipientId,
                principalId);
        return RoomDTO.getRoomDtoFromRoom(roomService.insert(principalId, recipientId));
    }
}
