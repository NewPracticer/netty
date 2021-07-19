package com.skyl.netty;

public class MyProtocolHeader {
    // 协议版本
    private int version;
    //设备编号
    private CharSequence machineNum;
    //消息类型
    private int msgType;

    public MyProtocolHeader(int version, CharSequence machineNum, int msgType) {
        this.version = version;
        this.machineNum = machineNum;
        this.msgType = msgType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public CharSequence getMachineNum() {
        return machineNum;
    }

    public void setMachineNum(CharSequence machineNum) {
        this.machineNum = machineNum;
    }


    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    @Override
    public String toString() {
        return "MyProtocolHeader{" +
                "version=" + version +
//                ", contentLength=" + contentLength +
//                ", serverName='" + serverName + '\'' +
                ", msgType='" + msgType + '\'' +
                '}';
    }

}
