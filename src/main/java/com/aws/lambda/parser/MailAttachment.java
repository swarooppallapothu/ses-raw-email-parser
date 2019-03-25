package com.aws.lambda.parser;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by Swaroop Pallapothu on Mar, 2019
 */
public class MailAttachment {

    private static final long serialVersionUID = 2L;

    private UUID mailAttachmentId;

    private MailContent mailContent;

    private String name;

    private Long size;

    private String filePath;

    private String fileUrl;

    private String contentType;

    private String fileType;

    private ZonedDateTime createdDate = ZonedDateTime.now(ZoneId.of("UTC"));

    private boolean s3Status;

    public UUID getMailAttachmentId() {
        return mailAttachmentId;
    }

    public void setMailAttachmentId(UUID mailAttachmentId) {
        this.mailAttachmentId = mailAttachmentId;
    }

    public MailContent getMailContent() {
        return mailContent;
    }

    public void setMailContent(MailContent mailContent) {
        this.mailContent = mailContent;
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
