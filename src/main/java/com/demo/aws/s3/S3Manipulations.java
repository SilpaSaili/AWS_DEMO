package com.demo.aws.s3;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Event;
import software.amazon.awssdk.services.s3.model.LambdaFunctionConfiguration;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NotificationConfiguration;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
public class S3Manipulations {

	static String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
	static String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";
	static String regionName = Region.US_EAST_1.toString();
	static AwsCredentials credentials = AwsBasicCredentials.create(System.getenv(AWS_ACCESS_KEY_ID),
			System.getenv(AWS_SECRET_ACCESS_KEY));
	static S3Client s3Client = S3Client.builder().region(Region.of(regionName))
			.credentialsProvider(StaticCredentialsProvider.create(credentials)).build();

	public void createBucket(String bucketName) {

		if (!doesBucketExists(bucketName)) {
			s3Client.createBucket(req -> req.bucket(bucketName));
			System.out.println("Bucket created");
		}
	}

	public void putObjectIntoBucket(String bucketName, String filePath) {
		if (doesBucketExists(bucketName)) {
			File file = new File(filePath);
			System.out.println("is file??? "+ file.isFile());
			s3Client.putObject(pReq -> pReq.bucket(bucketName).key(file.getName()), file.toPath());
			System.out.println("Object added to S3 Bucket :: " + bucketName);
		}
	}

	public void modifyBucketProperties(String bucketName) {
		// s3Client.bucket
	}

	public void getObjectFromBucket(String bucketName, String fileName, String downloadPath) {
		Path downloadPathObj = Paths.get(downloadPath);
		s3Client.getObject(getReq -> getReq.bucket(bucketName).key(fileName),
				ResponseTransformer.toFile(downloadPathObj));
	}

	public void copyObjectFromOneBucketToAnother(String srcBkt, String destBkt, String objName) {
		s3Client.copyObject(copyReq -> copyReq.sourceBucket(srcBkt).sourceKey(objName).destinationBucket(destBkt)
				.destinationKey(objName));
		System.out.println("File " + objName + " copied from " + srcBkt + "to " + destBkt);
	}

	public void listObjectsInABucket(String bucketName) {
		ListObjectsV2Response resp = s3Client.listObjectsV2(listObjV2Req -> listObjV2Req.bucket(bucketName));
		List<S3Object> contents = null != resp ? resp.contents() : new ArrayList<S3Object>();
		System.out.println("Number of objects in bucket " + bucketName + " are " + contents.size());
		contents.stream().forEach(System.out::println);
	}

	public void deleteObjectFromBucket(String bucketName, String fileName) {
		s3Client.deleteObject(delObjReq -> delObjReq.bucket(bucketName).key(fileName));
		System.out.println("Object " + fileName + " deleted from bucket " + bucketName);
	}

	public void invokeLambdaAfterS3Manipulation(String bucketName, String lambdaFnARN, String lambdaFnName,
			String srcArn, String action, String srcAcnt, String principal, String stmtId) {
		LambdaClient lambdaClient = LambdaClient.builder().build();
		lambdaClient.addPermission(req -> req.functionName(lambdaFnName).sourceArn(srcArn).sourceAccount(srcAcnt)
				.action(action).principal(principal).statementId(stmtId));
		PutBucketNotificationConfigurationResponse resp = s3Client.putBucketNotificationConfiguration(
				PutBucketNotificationConfigurationRequest.builder().bucket(bucketName)
						.notificationConfiguration(NotificationConfiguration.builder()
								.lambdaFunctionConfigurations(LambdaFunctionConfiguration.builder()
										.lambdaFunctionArn(lambdaFnARN).events(Event.S3_OBJECT_CREATED).build())
								.build())
						.build());
		System.out.println("Lambda response :: " + resp.responseMetadata());
	}

	private boolean doesBucketExists(String bucketName) {
		try {
			s3Client.headBucket(hReq -> hReq.bucket(bucketName));
			return true;
		} catch (NoSuchBucketException nsbe) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
