package com.cchat.common.base.data;

/**
 * Created by holand on 15/12/5.
 */
public class FileResult extends Result {
    private long id;
    private String roomId;
    private String fileName;
    private String filePath;

    private String targetPath;

    private double size;
    private String serverIp;
    private String port;
    private String senderPerrId;
    private String recieverPeerId;
    private String type;//normal start end continue pause error

    private boolean isSender;

    public FileResult() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSenderPerrId() {
        return senderPerrId;
    }

    public void setSenderPerrId(String senderPerrId) {
        this.senderPerrId = senderPerrId;
    }

    public String getRecieverPeerId() {
        return recieverPeerId;
    }

    public void setRecieverPeerId(String recieverPeerId) {
        this.recieverPeerId = recieverPeerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSender() {
        return isSender;
    }

    public void setIsSender(boolean isSender) {
        this.isSender = isSender;
    }

}
