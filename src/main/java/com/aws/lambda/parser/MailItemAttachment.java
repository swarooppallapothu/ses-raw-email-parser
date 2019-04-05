package com.aws.lambda.parser;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by Swaroop Pallapothu on Apr, 2019
 */
public class MailItemAttachment {

    private static final long serialVersionUID = 2L;

    private UUID mailItemAttachmentId;

    private MailItem mailItem;

    private String name;

    private Long size;

    private String filePath;

    private String fileUrl;

    private String contentType;

    private String fileType;

    private ZonedDateTime createdDate;

    private boolean s3Status;

    public UUID getMailItemAttachmentId() {
        return mailItemAttachmentId;
    }

    public void setMailItemAttachmentId(UUID mailItemAttachmentId) {
        this.mailItemAttachmentId = mailItemAttachmentId;
    }

    public MailItem getMailItem() {
        return mailItem;
    }

    public void setMailItem(MailItem mailItem) {
        this.mailItem = mailItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isS3Status() {
        return s3Status;
    }

    public void setS3Status(boolean s3Status) {
        this.s3Status = s3Status;
    }

}
