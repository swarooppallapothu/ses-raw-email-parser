package com.aws.lambda.parser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import com.aws.lambda.parser.util.MailParserUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.http.entity.ContentType;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Properties;
import java.util.stream.Collectors;

public class SESRawEmailParser extends MailParserUtil implements RequestStreamHandler {

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
                String messageId = event.getRecords().get(0).getSES().getMail().getMessageId();

                if (!StringUtils.isNullOrEmpty(messageId)) {
                    storeRequestStream(new ByteArrayInputStream(requestByteArr), messageId + ".json", context);
                } else {
                    throw new Exception("No Message Id genMs: " + genMs);
                }

                logger.log(messageId);

                if (!StringUtils.isNullOrEmpty(messageId)) {
                    S3Object s3Object = S3_CLIENT.getObject(new GetObjectRequest(
                            SRC_BKT, messageId));
                    if (s3Object != null) {
                        InputStream objectData = s3Object.getObjectContent();

                        MimeMessage mimeMessageObj = new MimeMessage(Session.getDefaultInstance(new Properties()), objectData);
                        MimeMessageParser mimeParser = new MimeMessageParser(mimeMessageObj);

                        mimeParser.parse();


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

    public MailContent mapMailContent(SESEvent sesEvent) {
        SESEvent.SESMail sesMail = sesEvent.getRecords().get(0).getSES().getMail();
        SESEvent.SESReceipt sesReceipt = sesEvent.getRecords().get(0).getSES().getReceipt();
        MailContent mailContent = new MailContent();
        mailContent.setMailId(parseMailId(sesMail.getCommonHeaders().getMessageId()));
        mailContent.setReferences(getHeaderValue(sesMail.getHeaders(), MailParserUtil.CONST_AWS_SES_RESPONSE_EMAIL_HEADER_REFERENCES));
        mailContent.setInReplyTo(getHeaderValue(sesMail.getHeaders(), MailParserUtil.CONST_AWS_SES_RESPONSE_EMAIL_HEADER_X_REPLY_TO));
        String sender = getHeaderValue(sesMail.getHeaders(), MailParserUtil.CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Sender);
        if (StringUtils.isNullOrEmpty(sender)) {
            sender = getHeaderValue(sesMail.getHeaders(), MailParserUtil.CONST_AWS_SES_RESPONSE_EMAIL_HEADER_From);
        }

        mailContent.setSender(parseEmailFromMailgunResponse(sender));
        mailContent.setSenderName(parseNamesFromMailgunResponse(sender));
        mailContent.setRecipients(parseEmailsFromMailgunResponseAsString(sesMail.getDestination()));
        mailContent.setRecipientNames(parseNamesFromMailgunResponseAsString(sesMail.getDestination()));
        mailContent.setMailFrom(getHeaderValue(sesMail.getHeaders(), MailParserUtil.CONST_AWS_SES_RESPONSE_EMAIL_HEADER_From));
        mailContent.setMailTo(getHeaderValue(sesMail.getHeaders(), MailParserUtil.CONST_AWS_SES_RESPONSE_EMAIL_HEADER_To));
        mailContent.setEnvelopeFrom(null); //TODO Need deep analysis
        mailContent.setMailCC(getHeaderValue(sesMail.getHeaders(), MailParserUtil.CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Cc));
        mailContent.setMailBCC(getHeaderValue(sesMail.getHeaders(), MailParserUtil.CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Bcc));
        mailContent.setMailSubject(getHeaderValue(sesMail.getHeaders(), MailParserUtil.CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Subject));
        mailContent.setStrippedText(null); //TODO Need deep analysis
        mailContent.setStrippedHtml(null); //TODO Need deep analysis
        mailContent.setStrippedSignature(null); //TODO Need deep analysis
        mailContent.setMailHeaders("[]"); //TODO Need deep analysis
        mailContent.setContentIdMap("{}");  //TODO Need deep analysis
        String mailDate = sesMail.getCommonHeaders().getDate();
        if (StringUtils.isNullOrEmpty(mailDate)) {
            mailDate = getHeaderValue(sesMail.getHeaders(), MailParserUtil.CONST_AWS_SES_RESPONSE_EMAIL_HEADER_Date);
        }
        mailContent.setMailRecievedDate(mailDate);
        mailContent.setMailDate(parseRfc2822DateString(mailContent.getMailRecievedDate()));
        mailContent.setMailReceivedDetails(null);//TODO Need deep analysis
        mailContent.setAllRecipientEmailsSet(parseEmailsFromMailgunResponse(mailContent, true));
        mailContent.setAllRecipientEmails(String.join(", ", mailContent.getAllRecipientEmailsSet()));
        return mailContent;

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
}
