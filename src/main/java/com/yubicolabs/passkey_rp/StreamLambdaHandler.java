package com.yubicolabs.passkey_rp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class StreamLambdaHandler implements RequestStreamHandler {

  public static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
  static {
    try {
      handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(PasskeyRpApplication.class);
    } catch (ContainerInitializationException e) {
      e.printStackTrace();
      throw new RuntimeException("Could not initialize Spring Boot application", e);
    }
  }

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    // TODO Auto-generated method stub
    handler.proxyStream(inputStream, outputStream, context);
  }

}
