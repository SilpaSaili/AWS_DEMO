package com.demo.aws.sns;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

@Component
public class SNSManipulations {

	public void createSNSTopic(String topicName, SnsClient snsClient) {
		CreateTopicResponse crtTpcResp = snsClient.createTopic(crtTopicReq -> crtTopicReq.name(topicName).build());
		if (null != crtTpcResp) {
			System.out.println("Created topic " + topicName + " ::: topic ARN " + crtTpcResp.topicArn());
		}
	}

	public void subscribeToTopic(String topicARN, SnsClient snsClient, String protocol, String endPoint) {
		SubscribeResponse result = snsClient
				.subscribe(subReq -> subReq.topicArn(topicARN).protocol(protocol).endpoint(endPoint).build());
		System.out.println("Subscription ARN: " + result.subscriptionArn() + "\n\n Status is "
				+ result.sdkHttpResponse().statusCode());
	}

	public void publishSNSMessage(String message, String subject, String topicArn, SnsClient snsClient,
			String lambdaFnName) {

		PublishResponse pubRes = snsClient.publish(pubReq -> pubReq.message(message).topicArn(topicArn).build());
		if (null != pubRes) {
			System.out.println("Published message  ::: " + pubRes.messageId());
			// invokeLambdaAfterSNSPublishMessage(lambdaFnName, message, subject, topicArn);
		}

	}

	public void publishSMSMessage(String message, String topicArn, SnsClient snsClient) {
		HashMap<String, String> attributes = new HashMap<>(1);
		attributes.put("DefaultSMSType", "Transactional");

		snsClient.setSMSAttributes(smsattrReq -> smsattrReq.attributes(attributes).build());
		snsClient.publish(pubReq -> pubReq.message(message).phoneNumber("+15312556813").build());
		System.out.println("Published message to phone  ::: " + message);
	}

	public void listSNSTopics(SnsClient snsClient) {
		ListTopicsResponse listTopicsResponse = snsClient.listTopics(listTpcReq -> listTpcReq.build());
		if(null != listTopicsResponse && listTopicsResponse.hasTopics()) {
			listTopicsResponse.topics().stream().forEach(topic -> System.out.println(topic.toString()));
		}
	}

	public void deleteSNSTopic(String topicARN, SnsClient snsClient) {
		snsClient.deleteTopic(delTopicReq -> delTopicReq.topicArn(topicARN).build());
	}

	public void unsubscribeFromTopic(SnsClient snsClient, String subsArn) {
		snsClient.unsubscribe(unSubReq -> unSubReq.subscriptionArn(subsArn).build());
	}

}
