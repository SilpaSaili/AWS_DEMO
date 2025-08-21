package com.demo.aws.lambda;

import java.util.Optional;

import org.springframework.stereotype.Component;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionResponse;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import software.amazon.awssdk.services.lambda.model.ListFunctionsResponse;
import software.amazon.awssdk.services.lambda.model.ResourceNotFoundException;
import software.amazon.awssdk.services.lambda.model.UpdateFunctionCodeResponse;
import software.amazon.awssdk.services.lambda.waiters.LambdaWaiter;
import software.amazon.awssdk.services.sns.SnsClient;

@Component
public class LambdaManipulations {

	public void createLambdaFunction(LambdaClient lambdaClient, String fnName, String bucketName, String bucketKey,
			String handler, String roleARN, String runtimeVersion) {

		if (!doesLambdaFunctionExists(lambdaClient, fnName)) {
			FunctionCode fnCode = FunctionCode.builder().s3Bucket(bucketName).s3Key(bucketKey).build();

			CreateFunctionResponse response = lambdaClient.createFunction(createFnReq -> createFnReq
					.functionName(fnName).description("Creating lambda function from Java code").code(fnCode)
					.role(roleARN).runtime(runtimeVersion).handler(handler).build());
			if (null != response) {
				System.out.println("Response data :: " + response.toString());
			}
		} else {
			System.out.println(fnName + " already exists");
		}
	}

	public void invokeLambdaFnFromCode(LambdaClient lambdaClient, String fnName, SdkBytes payload) {
		if (doesLambdaFunctionExists(lambdaClient, fnName)) {
			try {
				Optional<InvokeResponse> invokeResp = Optional
						.of(lambdaClient.invoke(invokeReq -> invokeReq.functionName(fnName).payload(payload).build()));
				invokeResp.ifPresent(invokeResp1 -> System.out.println(invokeResp1.payload().asUtf8String()));
			} catch (LambdaException le) {
				le.printStackTrace();
			}
		} else {
			System.out.println(fnName + " function does not exist");
		}
	}

	public void deleteLambdaFunction(LambdaClient lambdaClient, String functionName) {
		if (doesLambdaFunctionExists(lambdaClient, functionName)) {
			lambdaClient.deleteFunction(delFnReq -> delFnReq.functionName(functionName).build());
			System.out.println("Deleted lambda function -> " + functionName);
		} else {
			System.out.println(functionName + " does not exist");
		}
	}

	public void updateLambdaFunctionCode(LambdaClient lambdaClient, String fnName, String bucketName,
			String bucketKey) {
		if (doesLambdaFunctionExists(lambdaClient, fnName)) {
			UpdateFunctionCodeResponse response = lambdaClient.updateFunctionCode(updFnCodeReq -> updFnCodeReq
					.functionName(fnName).s3Bucket(bucketName).s3Key(bucketKey).build());
			LambdaWaiter waiter = lambdaClient.waiter();
			WaiterResponse<GetFunctionResponse> waiterResp = waiter
					.waitUntilFunctionUpdatedV2(getFnReq -> getFnReq.functionName(fnName).build());
			waiterResp.matched().response().ifPresent(System.out::println);
			System.out.println(response.lastModified());
		} else {
			System.out.println(fnName + " does not exist");
		}
	}

	public void updateLambdaFnConfig(LambdaClient lClient, String fnName) {
		if (doesLambdaFunctionExists(lClient, fnName)) {
			lClient.updateFunctionConfiguration(
					updFnConfigReq -> updFnConfigReq.functionName(fnName).description("New description").build());
		} else {
			System.out.println(fnName + " does not exist");
		}

	}

	public void listAllFunctions(LambdaClient lClient) {
		ListFunctionsResponse lResp = lClient.listFunctions();
		if (lResp.hasFunctions()) {
			lResp.functions().stream().forEach(config -> System.out.println(config.functionName()));
		}
	}
	
	public void invokeSNS(String message, Region region) {
		SnsClient snsCli = SnsClient.builder().region(region).build();
		snsCli.publish(pubReq -> pubReq.message(message).build());
	}

	private boolean doesLambdaFunctionExists(LambdaClient lambdaClient, String fnName) {
		boolean doesFunctionExists = true;
		try {
			lambdaClient.getFunction(getFnReq -> getFnReq.functionName(fnName).build());
		} catch (ResourceNotFoundException rne) {
			doesFunctionExists = false;
		}

		System.out.println("Function Response ::: " + doesFunctionExists);
		return doesFunctionExists;
	}
}
