package com.aws.lambda.parser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.activation.DataSource;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RawEmailParser implements RequestHandler<S3Event, Void> {

    public Void handleRequest(S3Event event, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            String dstBucket = "3s-poc-repo";

            S3EventNotification.S3EventNotificationRecord record = event.getRecords().get(0);

            String srcBucket = record.getS3().getBucket().getName();
            // Object key may have spaces or unicode non-ASCII characters.
            String srcKey = record.getS3().getObject().getKey()
                    .replace('+', ' ');
            srcKey = URLDecoder.decode(srcKey, "UTF-8");
            logger.log("SRC bucket: " + srcBucket + "\n" +
                    " srcKey: " + srcKey + "\n" +
                    " eventName: " + record.getEventName() + "\n" +
                    " eventSource: " + record.getEventSource());

            // Download the mail from S3 into a stream
            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();//new AmazonS3Client();
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(
                    srcBucket, srcKey));
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

                // Put attachments to target bucket
                if (mimeParser.hasAttachments()) {
                    List<String> attachments = new ArrayList<>();
                    for (DataSource ds : mimeParser.getAttachmentList()) {
                        String dstKey = ds.getName();
                        attachments.add(dstKey);
                    /*    byte[] bytes = IOUtils.toByteArray(ds.getInputStream());
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentLength(bytes.length);
                        metadata.setContentType(ds.getContentType());
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                        PutObjectRequest putObjectRequest = new PutObjectRequest(dstBucket, dstKey, byteArrayInputStream, metadata);
                        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                        PutObjectResult putObjectResult = s3Client.putObject(putObjectRequest);*/

                        // Uploading to S3 destination bucket
//                        logger.log("Writing to: " + dstBucket + "/" + dstKey);

                    }
                    logger.log(String.join("\n", attachments));
                }
            } else {
                logger.log("Event Object Doesn't Exists");
            }
        } catch (Exception e) {
            logger.log(e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }
}
