package com.demo.aws;

import java.util.Scanner;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.demo.aws.s3.S3Manipulations;

@SpringBootApplication
public class S3BucketStoreApplication {

	public static void main(String[] args) {
		// SpringApplication.run(S3BucketStoreApplication.class, args);
		ApplicationContext ctx = new AnnotationConfigApplicationContext(
				S3BucketStoreApplication.class.getPackageName());
		S3Manipulations s3Man = ctx.getBean(S3Manipulations.class);

		Scanner scan = new Scanner(System.in);

		/** Create a S3 bucket */
		System.out.println("Enter bucket name :: ");
		String bucketName = scan.nextLine();
		s3Man.createBucket(bucketName);

		/** Put an object into S3 bucket */

		System.out.println("Enter file path :: ");
		String filePath = scan.nextLine();
		s3Man.putObjectIntoBucket(bucketName, filePath);

		/** Invoking a lambda function after putting an object into S3 bucket */
		s3Man.invokeLambdaAfterS3Manipulation("s3-manipulations-bucket",
				"arn:aws:lambda:us-east-1:288761728457:function:lambdaFnWithS3Trigger", "lambdaFnWithS3Trigger",
				"arn:aws:s3:::s3-manipulations-bucket", "lambda:InvokeFunction", "288761728457", "s3.amazonaws.com",
				"ID-14");

		/** Download an object from S3 bucket */

		System.out.println("Enter name of file to be downloaded :: ");
		String fileName = scan.nextLine();
		System.out.println("Enter download path :: ");
		String downloadPath = scan.nextLine();

		s3Man.getObjectFromBucket(bucketName, fileName, downloadPath);

		/** Copy an object from one S3 bucket to another */

		System.out.println("Enter source bucket name :: ");
		String srcBucketName = scan.nextLine();

		System.out.println("Enter destination bucket name :: ");
		String destBucketName = scan.nextLine();

		System.out.println("Enter name of file to be copied :: ");
		String cpFileName = scan.nextLine();

		s3Man.copyObjectFromOneBucketToAnother(srcBucketName, destBucketName, cpFileName);

		s3Man.listObjectsInABucket(bucketName);

		/** Delete an object from S3 bucket */

		System.out.println("Enter name of file to be deleted:: ");
		String delFileName = scan.nextLine();
		s3Man.deleteObjectFromBucket(bucketName, delFileName);

		System.out.println("List of files in bucket after deleting the given file");

		s3Man.listObjectsInABucket(bucketName);

		scan.close();
	}

}
