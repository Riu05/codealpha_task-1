package com.riya.chatbot;

public class ChatBot {

    public String getResponse(String input) {
        return com.riya.chatbot.GeminiClient.getGeminiReply(input);
    }
}

