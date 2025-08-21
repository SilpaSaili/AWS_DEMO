package com.demo.aws;

import java.util.Scanner;

import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.demo.aws.lambda.LambdaManipulations;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;

@SpringBootApplication
public class LambdaApplication {

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(LambdaApplication.class.getPackageName());
		LambdaManipulations lManipulat = ctx.getBean(LambdaManipulations.class);
		LambdaClient lClient = LambdaClient.builder().build();

		/** Create Lambda Function from code */
		/*
		 * String roleARN = "arn:aws:iam::288761728457:role/LambdaBasicExecutionRole";
		 * Runtime.Version version = Runtime.version(); String runtimeVersion = "java" +
		 * String.valueOf(version.version().get(0)); String handler =
		 * "com.demo.lambda.Lambda_Learning.s3.LambdaS3Handler::lambdaFnToPushToS3";
		 * 
		 * lManipulat.createLambdaFunction(lClient, "lambdaFnToPushToS3",
		 * "s3-manipulations-bucket", "Lambda_Learning-0.0.1-SNAPSHOT.jar", handler,
		 * roleARN, runtimeVersion);
		 * 
		 * Thread.sleep(3000);
		 */

		/** Invoke Lambda Function from code */

		/*
		 * JSONObject jsonObj = new JSONObject(); jsonObj.put("inputKey",
		 * "testing :: "); SdkBytes payLoad =
		 * SdkBytes.fromUtf8String(jsonObj.toString());
		 * lManipulat.invokeLambdaFnFromCode(lClient, "lambdaFnToPublishSNSMsg",
		 * payLoad);
		 */

		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter bucket name :: ");
		String bucketName = scanner.nextLine();
		System.out.println("Enter file path :: ");
		String filePath = scanner.nextLine();
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("bucketName", bucketName);
		jsonObj.put("filePath", filePath);
		SdkBytes payLoad = SdkBytes.fromUtf8String(jsonObj.toString());
		lManipulat.invokeLambdaFnFromCode(lClient, "lambdaFnToPushToS3", payLoad);
		scanner.close();

		/** Delete Lambda Function from code */
		/*
		 * lManipulat.deleteLambdaFunction(lClient, "lambdaFnWithSNSTrigger");
		 * 
		 *//** Update Lambda Function's Code */
		/*
		 * lManipulat.updateLambdaFunctionCode(lClient, "lambdaFnWithS3Trigger",
		 * "s3-manipulations-bucket", "Lambda_Learning-0.0.1-SNAPSHOT.jar");
		 * 
		 *//** Update Lambda Function's configuration from code */
		/*
		 * lManipulat.updateLambdaFnConfig(lClient, "lambdaFnWithS3Trigger");
		 * 
		 *//** List all lambda functions *//*
											 * lManipulat.listAllFunctions(lClient);
											 */
		lClient.close();

	}
}
