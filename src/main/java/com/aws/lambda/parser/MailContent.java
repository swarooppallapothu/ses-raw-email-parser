package com.aws.lambda.parser;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Swaroop Pallapothu on Mar, 2019
 */
public class MailContent {

    private static final long serialVersionUID = 1L;

    private UUID mailContentId;

    private String mailFullUrl;

    private MessageWorkflowStatus mailWorkflowStatus;

    private String mailId;

    private String references;

    private String inReplyTo;

    private String sender;

    private String recipients;

    private String mailFrom;

    private String mailTo;

    private String envelopeFrom;

    private String mailCC;

    private String mailBCC;

    private String senderName;

    private String recipientNames;

    private String allRecipientEmails;

    private String mailSubject;

    private String strippedText;

    private String strippedHtml;

    private String strippedSignature;

    //TODO use custom mapper
    private String mailHeaders;

    private String contentIdMap;

    private String mailContentFileUrl;

    private String mailRecievedDate;

    private String mailReceivedDetails;

    private int attachmentsCount;

    private ZonedDateTime mailDate;

    private ZonedDateTime createdDate = ZonedDateTime.now(ZoneId.of("UTC"));

    private ZonedDateTime lastModifiedDate;

    private Set<String> allRecipientEmailsSet;

    private List<MailAttachment> mailAttachmentList;

    public UUID getMailContentId() {
        return mailContentId;
    }

    public void setMailContentId(UUID mailContentId) {
        this.mailContentId = mailContentId;
    }

    public String getMailFullUrl() {
        return mailFullUrl;
    }

    public void setMailFullUrl(String mailFullUrl) {
        this.mailFullUrl = mailFullUrl;
    }

    public MessageWorkflowStatus getMailWorkflowStatus() {
        return mailWorkflowStatus;
    }

    public void setMailWorkflowStatus(MessageWorkflowStatus mailWorkflowStatus) {
        this.mailWorkflowStatus = mailWorkflowStatus;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getEnvelopeFrom() {
        return envelopeFrom;
    }

    public void setEnvelopeFrom(String envelopeFrom) {
        this.envelopeFrom = envelopeFrom;
    }

    public String getMailCC() {
        return mailCC;
    }

    public void setMailCC(String mailCC) {
        this.mailCC = mailCC;
    }

    public String getMailBCC() {
        return mailBCC;
    }

    public void setMailBCC(String mailBCC) {
        this.mailBCC = mailBCC;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientNames() {
        return recipientNames;
    }

    public void setRecipientNames(String recipientNames) {
        this.recipientNames = recipientNames;
    }

    public String getAllRecipientEmails() {
        return allRecipientEmails;
    }

    public void setAllRecipientEmails(String allRecipientEmails) {
        this.allRecipientEmails = allRecipientEmails;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getStrippedText() {
        return strippedText;
    }

    public void setStrippedText(String strippedText) {
        this.strippedText = strippedText;
    }

    public String getStrippedHtml() {
        return strippedHtml;
    }

    public void setStrippedHtml(String strippedHtml) {
        this.strippedHtml = strippedHtml;
    }

    public String getStrippedSignature() {
        return strippedSignature;
    }

    public void setStrippedSignature(String strippedSignature) {
        this.strippedSignature = strippedSignature;
    }

    public String getMailHeaders() {
        return mailHeaders;
    }

    public void setMailHeaders(String mailHeaders) {
        this.mailHeaders = mailHeaders;
    }

    public String getContentIdMap() {
        return contentIdMap;
    }

    public void setContentIdMap(String contentIdMap) {
        this.contentIdMap = contentIdMap;
    }

    public String getMailContentFileUrl() {
        return mailContentFileUrl;
    }

    public void setMailContentFileUrl(String mailContentFileUrl) {
        this.mailContentFileUrl = mailContentFileUrl;
    }

    public String getMailRecievedDate() {
        return mailRecievedDate;
    }

    public void setMailRecievedDate(String mailRecievedDate) {
        this.mailRecievedDate = mailRecievedDate;
    }

    public String getMailReceivedDetails() {
        return mailReceivedDetails;
    }

    public void setMailReceivedDetails(String mailReceivedDetails) {
        this.mailReceivedDetails = mailReceivedDetails;
    }

    public int getAttachmentsCount() {
        return attachmentsCount;
    }

    public void setAttachmentsCount(int attachmentsCount) {
        this.attachmentsCount = attachmentsCount;
    }

    public ZonedDateTime getMailDate() {
        return mailDate;
    }

    public void setMailDate(ZonedDateTime mailDate) {
        this.mailDate = mailDate;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<String> getAllRecipientEmailsSet() {
        return allRecipientEmailsSet;
    }

    public void setAllRecipientEmailsSet(Set<String> allRecipientEmailsSet) {
        this.allRecipientEmailsSet = allRecipientEmailsSet;
    }

    public List<MailAttachment> getMailAttachmentList() {
        return mailAttachmentList;
    }

    public void setMailAttachmentList(List<MailAttachment> mailAttachmentList) {
        this.mailAttachmentList = mailAttachmentList;
    }

}
