package com.aws.lambda.parser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.http.entity.ContentType;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class SESRawEmailParser implements RequestStreamHandler {

    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_REFERENCES = "X-References";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_X_REPLY_TO = "X-Reply-To";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_From = "From";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_To = "To";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Sender = "Sender";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Cc = "Cc";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Bcc = "Bcc";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Subject = "Subject";
    public static final String CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Date = "Date";
    private final static ObjectMapper objectMapper;
    private final static AmazonS3 S3_CLIENT;
    private final static String SRC_BKT;
    private final static String TGT_BKT;

    static {
        objectMapper = new ObjectMapper(); // can reuse, share globally
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        S3_CLIENT = AmazonS3ClientBuilder.defaultClient();
        SRC_BKT = "lucroview-net-email-repo";
        TGT_BKT = "lucroview-net-store";
        uniRestConfiguration();
    }

    public static void uniRestConfiguration() {
        try {
            Unirest.setObjectMapper(new com.mashape.unirest.http.ObjectMapper() {
                private ObjectMapper jacksonObjectMapper
                        = new ObjectMapper();

                public <T> T readValue(String value, Class<T> valueType) {
                    try {
                        return jacksonObjectMapper.readValue(value, valueType);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                public String writeValue(Object value) {
                    try {
                        return jacksonObjectMapper.writeValueAsString(value);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        LambdaLogger logger = context.getLogger();
        long genMs = System.currentTimeMillis();
        byte[] requestByteArr = null;
        try {
            if (inputStream != null) {
                requestByteArr = IOUtils.toByteArray(inputStream);

                logger.log("genMs: " + genMs);
//                logger.log("IOUtils.toByteArray(is).length: " + IOUtils.toByteArray(is).length);
                String jsonString = isToString(new ByteArrayInputStream(requestByteArr));
//                logger.log("IOUtils.toByteArray(is).length: " + IOUtils.toByteArray(is).length);
                logger.log(jsonString);
                SESEvent event = objectMapper.readValue(jsonString, SESEvent.class);
                String sesMessageId = event.getRecords().get(0).getSES().getMail().getMessageId();

                if (!StringUtils.isNullOrEmpty(sesMessageId)) {
                    storeRequestStream(new ByteArrayInputStream(requestByteArr), sesMessageId + ".json", context);
                } else {
                    throw new Exception("No Message Id genMs: " + genMs);
                }

                logger.log(sesMessageId);

                if (!StringUtils.isNullOrEmpty(sesMessageId)) {
                    S3Object s3Object = S3_CLIENT.getObject(new GetObjectRequest(
                            SRC_BKT, sesMessageId));
                    if (s3Object != null) {
                        InputStream objectData = s3Object.getObjectContent();

                        MimeMessage mimeMessageObj = new MimeMessage(Session.getDefaultInstance(new Properties()), objectData);
                        MimeMessageParser mimeParser = new MimeMessageParser(mimeMessageObj);

                        mimeParser.parse();

                        MailItem mailItem = mapMailItem(event);
                        mailItem.setSesMessageId(sesMessageId);
                        mailItem.setRecipients(event.getRecords().get(0).getSES().getReceipt().getRecipients());
                        mailItem.setStrippedText(mimeParser.getPlainContent());
                        mailItem.setStrippedHtml(mimeParser.getHtmlContent());
                        mailItem.setAttachmentsCount(mimeParser.hasAttachments() ? mimeParser.getAttachmentList().size() : 0);

                        HttpResponse<JsonNode> postResponse = Unirest.post("https://enpp9tyg024u.x.pipedream.net?" + sesMessageId)
                                .header("accept", "application/json")
                                .header("Content-Type", "application/json")
                                .body(mailItem)
                                .asJson();

                        logger.log("Parsed Response: " + "\n" +
                                objectMapper.writeValueAsString(mailItem));


                        logger.log(" MessageID: " + mimeMessageObj.getMessageID() + "\n" +
                                " To: " + mimeParser.getTo() + "\n" +
                                " From: " + mimeParser.getFrom() + "\n" +
                                " Subject: " + mimeParser.getSubject() + "\n" +
                                " HtmlContent: " + mimeParser.getHtmlContent() + "\n" +
                                " PlainContent: " + mimeParser.getPlainContent() + "\n" +
                                " SentDate: " + mimeMessageObj.getSentDate() + "\n" +
                                " Attachments Counr: " + mimeParser.hasAttachments() + "\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.log(e.getMessage());
            if (requestByteArr != null) {
                storeRequestStream(new ByteArrayInputStream(requestByteArr), "invalid_stream_" + genMs, context);
            }
            throw new RuntimeException(e);
        }
    }

    public MailItem mapMailItem(SESEvent sesEvent) {
        SESEvent.SESMail sesMail = sesEvent.getRecords().get(0).getSES().getMail();
        SESEvent.SESReceipt sesReceipt = sesEvent.getRecords().get(0).getSES().getReceipt();
        MailItem mailItem = new MailItem();
        mailItem.setMessageId(sesMail.getCommonHeaders().getMessageId());
        mailItem.setReferences(getHeaderValue(sesMail.getHeaders(), CONST_AWS_SES_RESPONSE_EMAIL_HEADER_REFERENCES));
        mailItem.setInReplyTo(getHeaderValue(sesMail.getHeaders(), CONST_AWS_SES_RESPONSE_EMAIL_HEADER_X_REPLY_TO));
        String sender = getHeaderValue(sesMail.getHeaders(), CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Sender);
        if (StringUtils.isNullOrEmpty(sender)) {
            sender = getHeaderValue(sesMail.getHeaders(), CONST_AWS_SES_RESPONSE_EMAIL_HEADER_From);
        }

        mailItem.setSender(sender);
        mailItem.setMailFrom(getHeaderValue(sesMail.getHeaders(), CONST_AWS_SES_RESPONSE_EMAIL_HEADER_From));
        mailItem.setMailTo(getHeaderValue(sesMail.getHeaders(), CONST_AWS_SES_RESPONSE_EMAIL_HEADER_To));
        mailItem.setMailCc(getHeaderValue(sesMail.getHeaders(), CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Cc));
        mailItem.setMailBcc(getHeaderValue(sesMail.getHeaders(), CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Bcc));
        mailItem.setMailSubject(getHeaderValue(sesMail.getHeaders(), CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Subject));
        mailItem.setStrippedText(null); //TODO Need deep analysis
        mailItem.setStrippedHtml(null); //TODO Need deep analysis
        String mailDate = sesMail.getCommonHeaders().getDate();
        if (StringUtils.isNullOrEmpty(mailDate)) {
            mailDate = getHeaderValue(sesMail.getHeaders(), CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Date);
        }
        mailItem.setMailReceivedDate(mailDate);
        return mailItem;
    }

    public String isToString(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
    }

    public void storeRequestStream(InputStream is, String dstKey, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(is.available());
            metadata.setContentType(ContentType.APPLICATION_JSON.getMimeType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(TGT_BKT, dstKey, is, metadata);
            PutObjectResult putObjectResult = S3_CLIENT.putObject(putObjectRequest);

        } catch (Exception e) {
            logger.log(String.join("\n", "Exception occurred in method storeRequestStream: ", e.getMessage()));
            throw new RuntimeException(e);
        }
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
