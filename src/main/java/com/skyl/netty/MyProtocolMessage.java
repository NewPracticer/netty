package com.skyl.netty;

public class MyProtocolMessage {

    private MyProtocolHeader myProtocolHeader;
    private String content;

    public MyProtocolMessage(MyProtocolHeader myProtocolHeader, String content) {
        this.myProtocolHeader = myProtocolHeader;
        this.content = content;
    }

    public MyProtocolHeader getMyProtocolHeader() {
        return myProtocolHeader;
    }

    public void setMyProtocolHeader(MyProtocolHeader myProtocolHeader) {
        this.myProtocolHeader = myProtocolHeader;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MyProtocolMessage{" +
                "myProtocolHeader=" + myProtocolHeader +
                ", content='" + content + '\'' +
                '}';
    }

}
