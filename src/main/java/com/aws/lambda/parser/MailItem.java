package com.aws.lambda.parser;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Swaroop Pallapothu on Mar, 2019
 */
public class MailItem {

    private static final long serialVersionUID = 1L;

    private UUID mailItemId;

    private UUID parentId;

    private UUID rootId;

    private String sesMessageId;

    private String messageId;

    private String inReplyTo;

    private String references;

    private String sender;

    private String fromEmail;

    private String toEmails;

    private String ccEmails;

    private String bccEmails;

    private String mailFrom;

    private String mailTo;

    private String mailCc;

    private String mailBcc;

    private String mailSubject;

    private String strippedText;

    private String strippedHtml;

    private String mailContentFileUrl;

    private String mailReceivedDate;

    private int attachmentsCount;

    private boolean sentFlag;

    private boolean inboxActive;

    private boolean outboxActive;

    private boolean readFlag;

    private boolean answeredFlag;

    private boolean deletedFlag;

    private boolean flaggedFlag;

    private boolean draftFlag;

    private ZonedDateTime mailDate;

    private ZonedDateTime createdDate = ZonedDateTime.now(ZoneId.of("UTC"));

    private ZonedDateTime seenDate;

    private ZonedDateTime deletedDate;

    /*We should set with sending request*/
    private String recipient;

    private List<String> recipients;

    private Set<String> allEmails;

    private boolean sendRequest;

    public UUID getMailItemId() {
        return mailItemId;
    }

    public void setMailItemId(UUID mailItemId) {
        this.mailItemId = mailItemId;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public UUID getRootId() {
        return rootId;
    }

    public void setRootId(UUID rootId) {
        this.rootId = rootId;
    }

    public String getSesMessageId() {
        return sesMessageId;
    }

    public void setSesMessageId(String sesMessageId) {
        this.sesMessageId = sesMessageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToEmails() {
        return toEmails;
    }

    public void setToEmails(String toEmails) {
        this.toEmails = toEmails;
    }

    public String getCcEmails() {
        return ccEmails;
    }

    public void setCcEmails(String ccEmails) {
        this.ccEmails = ccEmails;
    }

    public String getBccEmails() {
        return bccEmails;
    }

    public void setBccEmails(String bccEmails) {
        this.bccEmails = bccEmails;
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

    public String getMailCc() {
        return mailCc;
    }

    public void setMailCc(String mailCc) {
        this.mailCc = mailCc;
    }

    public String getMailBcc() {
        return mailBcc;
    }

    public void setMailBcc(String mailBcc) {
        this.mailBcc = mailBcc;
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

    public String getMailContentFileUrl() {
        return mailContentFileUrl;
    }

    public void setMailContentFileUrl(String mailContentFileUrl) {
        this.mailContentFileUrl = mailContentFileUrl;
    }

    public String getMailReceivedDate() {
        return mailReceivedDate;
    }

    public void setMailReceivedDate(String mailReceivedDate) {
        this.mailReceivedDate = mailReceivedDate;
    }

    public int getAttachmentsCount() {
        return attachmentsCount;
    }

    public void setAttachmentsCount(int attachmentsCount) {
        this.attachmentsCount = attachmentsCount;
    }

    public boolean isSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(boolean sentFlag) {
        this.sentFlag = sentFlag;
    }

    public boolean isInboxActive() {
        return inboxActive;
    }

    public void setInboxActive(boolean inboxActive) {
        this.inboxActive = inboxActive;
    }

    public boolean isOutboxActive() {
        return outboxActive;
    }

    public void setOutboxActive(boolean outboxActive) {
        this.outboxActive = outboxActive;
    }

    public boolean isReadFlag() {
        return readFlag;
    }

    public void setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
    }

    public boolean isAnsweredFlag() {
        return answeredFlag;
    }

    public void setAnsweredFlag(boolean answeredFlag) {
        this.answeredFlag = answeredFlag;
    }

    public boolean isDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    public boolean isFlaggedFlag() {
        return flaggedFlag;
    }

    public void setFlaggedFlag(boolean flaggedFlag) {
        this.flaggedFlag = flaggedFlag;
    }

    public boolean isDraftFlag() {
        return draftFlag;
    }

    public void setDraftFlag(boolean draftFlag) {
        this.draftFlag = draftFlag;
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

    public ZonedDateTime getSeenDate() {
        return seenDate;
    }

    public void setSeenDate(ZonedDateTime seenDate) {
        this.seenDate = seenDate;
    }

    public ZonedDateTime getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(ZonedDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public Set<String> getAllEmails() {
        return allEmails;
    }

    public void setAllEmails(Set<String> allEmails) {
        this.allEmails = allEmails;
    }

    public boolean isSendRequest() {
        return sendRequest;
    }

    public void setSendRequest(boolean sendRequest) {
        this.sendRequest = sendRequest;
    }
}
