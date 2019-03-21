package com.aws.lambda.parser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Properties;
import java.util.stream.Collectors;

public class SESRawEmailParser implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        LambdaLogger logger = context.getLogger();
        try {
            if (inputStream != null) {
                ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
                String jsonString = isToString(inputStream);
                logger.log(jsonString);
                SESEvent event = mapper.readValue(jsonString, SESEvent.class);
                String messageId = event.getRecords().get(0).getSES().getMail().getMessageId();
                logger.log(messageId);

                if (!StringUtils.isNullOrEmpty(messageId)) {
                    String srcBucket = "lucroview-net-email-repo";
                    AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();//new AmazonS3Client();
                    S3Object s3Object = s3Client.getObject(new GetObjectRequest(
                            srcBucket, messageId));
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
            throw new RuntimeException(e);
        }
    }

    public String isToString(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
    }
}
