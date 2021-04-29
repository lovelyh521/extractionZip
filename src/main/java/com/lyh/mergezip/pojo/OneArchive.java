package com.lyh.mergezip.pojo;

public class OneArchive {
    private String path;
    private byte[] bytes;

    public OneArchive(String path, byte[] bytes) {
        this.path = path;
        this.bytes = bytes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
