package com.example.WaterDelivery.services;

import com.example.WaterDelivery.providers.Message;
import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void sendMessage(Message message) {
        messageRepository.save(message);
    }

    public void sendMessage(Person sender, Person receiver, String content, String encryptionMethod) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setEncryptionMethod(encryptionMethod);
        messageRepository.save(message);
    }

    public List<Message> getMessagesByReceiver(Person receiver) {
        return messageRepository.findByReceiver(receiver);
    }

    public List<Message> getMessagesBySender(Person sender) {
        return messageRepository.findBySender(sender);
    }

    public List<Message> getMessages(Person sender, Person receiver) {
        return messageRepository.findBySenderAndReceiverOrderByIdAsc(sender, receiver);
    }

    public Optional<Message> getMessageById(int id) {
        return messageRepository.findById(id);
    }
}
