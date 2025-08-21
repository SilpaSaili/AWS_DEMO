package com.demo.aws;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.demo.aws.sns.SNSManipulations;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.sns.SnsClient;

public class SNSApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SNSApplication.class.getPackageName());
		SNSManipulations snsManipulations = ctx.getBean(SNSManipulations.class);
		SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();
		/** Create SNS Topic */
		snsManipulations.createSNSTopic("snsTopic1", snsClient);

		/** Subscribe Email to SNS Topic */
		snsManipulations.subscribeToTopic("arn:aws:sns:us-east-1:288761728457:snsTopic1", snsClient, "email",
				"silpa.saili.b@gmail.com");

		/** Subscribe lambda to SNS Topic */
		snsManipulations.subscribeToTopic("arn:aws:sns:us-east-1:288761728457:snsTopic1", snsClient, "lambda",
				"arn:aws:lambda:us-east-1:288761728457:function:lambdaFnWithSNSTrigger");

		/** Subscribe phone number to SNS Topic */
		snsManipulations.subscribeToTopic("arn:aws:sns:us-east-1:288761728457:MyTopic2", snsClient, "sms",
				"+15312556813");

		/** Publish message to SNS Topic */
		snsManipulations.publishSNSMessage("6th SNS message", "SNS subject 6 ",
				"arn:aws:sns:us-east-1:288761728457:snsTopic1", snsClient, "lambdaFnWithSNSTrigger");

		/** Publish SMS to phone through SNS Topic */
		snsManipulations.publishSMSMessage("SNS message to phone", "arn:aws:sns:us-east-1:288761728457:snsTopic1",
				snsClient);

		/** List all SNS Topics */
		snsManipulations.listSNSTopics(snsClient);

		/** Un-subscribe from SNS Topic */
		snsManipulations.unsubscribeFromTopic(snsClient,
				"arn:aws:sns:us-east-1:288761728457:MyTopic2:ff1544e4-c16c-4fb2-8f1d-aeb3c0825af7");

		/** Delete SNS Topic */
		snsManipulations.deleteSNSTopic("arn:aws:sns:us-east-1:288761728457:MyTopic2", snsClient);
		snsManipulations.listSNSTopics(snsClient);

	}
}
