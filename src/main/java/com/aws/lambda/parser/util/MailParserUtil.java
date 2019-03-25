package com.aws.lambda.parser.util;

import com.amazonaws.util.CollectionUtils;
import com.amazonaws.util.StringUtils;
import com.aws.lambda.parser.MailContent;
import com.aws.lambda.parser.SESEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by Swaroop Pallapothu on Mar, 2019
 */
public class MailParserUtil {

    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_REFERENCES = "X-References";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_X_REPLY_TO = "X-Reply-To";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_From = "From";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_To = "To";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Sender = "Sender";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Cc = "Cc";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Bcc = "Bcc";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Subject = "Subject";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Date = "Date";

    public static final SimpleDateFormat rfc2822DateFormats[] = new SimpleDateFormat[]{
            new SimpleDateFormat("EEE, d MMM yy HH:mm:ss Z"),
            new SimpleDateFormat("EEE, d MMM yy HH:mm:ss Z (z)")
    };

    public String parseMessageUrl(String url) {
        String messageId = null;
        if (url != null && !url.trim().isEmpty()) {
            String[] urlTokens = url.split("/");
            messageId = urlTokens[urlTokens.length - 1];
        }
        return messageId;
    }

    public String getJSONNodeFieldValue(JsonNode jsonNode, String field) {
        String value = null;
        if (jsonNode.has(field)) {
            if (jsonNode.get(field).getNodeType() == JsonNodeType.ARRAY) {
                value = jsonNode.get(field).toString();
            } else {
                value = jsonNode.get(field).asText();
            }
        }
        return value;
    }

    public ZonedDateTime parseRfc2822DateString(String dateString) {
        ZonedDateTime zonedDateTime = null;
        for (SimpleDateFormat sdf : rfc2822DateFormats) {
            Date date = null;
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e) {
                // Don't care, we'll just run through all
            }
            if (date != null) {
                return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
            }
        }
        return zonedDateTime;
    }

    public String parseNamesFromMailgunResponse(String value) {
        String parsedNames = "";
        if (StringUtils.isNullOrEmpty(value)) {
            return value;
        }
        value = value.trim();
        if (value.contains(", ")) {
            String[] splitedVals = value.split(", ");
            String[] names = new String[splitedVals.length];
            for (int itemInd = 0; itemInd < splitedVals.length; itemInd++) {
                String parsedName = parseNameFromMailgunResponse(splitedVals[itemInd]);
                names[itemInd] = parsedName == null || parsedName.equals(splitedVals[itemInd]) ? "" : parsedName;
            }
            parsedNames = String.join(",", names);
        } else {
            String parsedName = parseNameFromMailgunResponse(value);
            parsedNames = parsedName == null || parsedName.equals(value) ? "" : parsedName;
        }
        return parsedNames;
    }

    public List<String> parseNamesFromMailgunResponse(List<String> value) {
        List<String> parsedNames = new ArrayList<>();
        if (CollectionUtils.isNullOrEmpty(value)) {
            return parsedNames;
        }

        for (String email : value) {
            String parsedName = parseEmailFromMailgunResponse(email);
            parsedNames.add(StringUtils.isNullOrEmpty(parsedName) || parsedName.equals(email) ? "" : parsedName);

        }

        return parsedNames;
    }

    public String parseNamesFromMailgunResponseAsString(List<String> value) {
        return String.join(", ", parseNamesFromMailgunResponse(value));
    }

    public String parseNameFromMailgunResponse(String value) {
        if (StringUtils.isNullOrEmpty(value)) {
            return value;
        }
        value = value.trim().replaceAll("\t", " ");
        if (value.contains(" <") && value.endsWith(">")) {
            value = value.split(" <")[0].trim();
        }
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.replaceAll("\"", "");
        }
        return value;
    }

    public Set<String> parseEmailsFromMailgunResponse(MailContent mailContent, boolean includeSender) {
        Set<String> emails = new LinkedHashSet<>();
        if (includeSender) {
            emails.addAll(parseEmailsFromMailgunResponse(mailContent.getSender()));
        }
        emails.addAll(parseEmailsFromMailgunResponse(mailContent.getMailTo()));
        emails.addAll(parseEmailsFromMailgunResponse(mailContent.getRecipients()));
        emails.addAll(parseEmailsFromMailgunResponse(mailContent.getMailCC()));
        emails.addAll(parseEmailsFromMailgunResponse(mailContent.getMailBCC()));
        return emails;
    }

    public Set<String> parseEmailsFromMailgunResponse(String value) {
        Set<String> parsedEmails = new LinkedHashSet();
        if (StringUtils.isNullOrEmpty(value)) {
            return parsedEmails;
        }
        value = value.trim();
        if (value.contains(", ")) {
            String[] splitedVals = value.split(", ");
            for (int itemInd = 0; itemInd < splitedVals.length; itemInd++) {
                String name = parseEmailFromMailgunResponse(splitedVals[itemInd]);
                parsedEmails.add(name == null ? "" : name);
            }
        } else {
            parsedEmails.add(parseEmailFromMailgunResponse(value));
        }
        return parsedEmails;
    }

    public List<String> parseEmailsFromMailgunResponse(List<String> value) {
        List<String> parsedEmails = new ArrayList<>();
        if (CollectionUtils.isNullOrEmpty(value)) {
            return parsedEmails;
        }

        for (String email : value) {
            String parsedEmail = parseEmailFromMailgunResponse(email);
            parsedEmails.add(StringUtils.isNullOrEmpty(parsedEmail) ? "" : parsedEmail);

        }
        return parsedEmails;
    }

    public String parseEmailsFromMailgunResponseAsString(List<String> value) {
        return String.join(", ", parseEmailsFromMailgunResponse(value));
    }

    public String parseEmailFromMailgunResponse(String value) {
        if (StringUtils.isNullOrEmpty(value)) {
            return value;
        }
        value = value.trim().replaceAll("\t", " ");
        if (value.contains(" <") && value.endsWith(">")) {
            value = value.split(" <")[1].replaceAll(">", "").trim();
        }
        return value;
    }

    public List<String> parseMailIds(String value) {
        List<String> parsedMailIds = new ArrayList();
        if (StringUtils.isNullOrEmpty(value)) {
            return parsedMailIds;
        }
        value = value.trim();
        if (value.contains(" ")) {
            String[] splitedVals = value.split(" ");
            for (int itemInd = 0; itemInd < splitedVals.length; itemInd++) {
                parsedMailIds.add(parseMailId(splitedVals[itemInd]));
            }
        } else {
            parsedMailIds.add(parseMailId(value));
        }
        return parsedMailIds;
    }

    public String parseMailId(String value) {
        String parsedMailId = "";
        if (StringUtils.isNullOrEmpty(value)) {
            return value;
        }
        parsedMailId = value.trim().replaceAll("\t", " ");
        if (parsedMailId.startsWith("<") && parsedMailId.endsWith(">")) {
            parsedMailId = parsedMailId.substring(1, parsedMailId.length() - 1).trim();
        }
        return parsedMailId;
    }

    public String getMailAttachementPath(String mailId, String fileName) throws Exception {
        return mailId + "/attachments/" + UUID.randomUUID() + "_" + URLEncoder.encode(fileName, "UTF-8");
    }

    public String getHeaderValue(List<SESEvent.MessageHeader> messageHeaders, String header) {
        String value = messageHeaders.stream()
                .filter(messageHeader ->
                        messageHeader.getName().equalsIgnoreCase(header)
                ).findFirst()
                .map(messageHeader ->
                        messageHeader.getValue()
                ).orElse(null);

        return value;
    }
}
