package com.isimo.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.eclipse.jetty.util.BlockingArrayQueue;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.isimo.core.Action;
import com.isimo.core.SpringContext;
import com.isimo.core.TestExecutionManager;
import com.isimo.core.event.Event;
import com.isimo.core.event.Message;
import com.isimo.core.event.Response;

public abstract class DebugThreadBase<E extends Event, R extends Response> extends Thread {
	ServerSocket serverSocket;
	Socket clientSocket;
	PrintWriter writer;
	BufferedReader reader;
	Gson gson;
	Class<R> responseClass;
	Class<E> eventClass;
	E e = null;
	BlockingQueue<E> eventQueue = new BlockingArrayQueue<E>();
	
	int port;
	TestExecutionManager testExecutionManager;
	
	public DebugThreadBase(int port) {
		super();
		try {
			String eventClassName = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].toString();
			String respClassName = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1].toString();
			this.eventClass = (Class<E>) Class.forName(eventClassName.replace("class ", ""));
			this.responseClass = (Class<R>) Class.forName(respClassName.replace("class ", ""));
			this.port = port;
			testExecutionManager = SpringContext.getBean(TestExecutionManager.class);
			serverSocket = new ServerSocket(port);
			gson = new GsonBuilder()
				    .setExclusionStrategies(new ExclusionStrategy() {
				    	@Override
				    	public boolean shouldSkipField(FieldAttributes f) {
				    		String name = f.getName();
				    		if(f.hasModifier(Modifier.TRANSIENT) || f.hasModifier(Modifier.VOLATILE) || f.hasModifier(Modifier.STATIC)) {
				    			return true;
				    		}
				    		return false;
				    	}
				    	
				    	@Override
				    	public boolean shouldSkipClass(Class<?> clazz) {
				    		return false;
				    	}
				    }).create();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	void waitForClient() {
		try {
			testExecutionManager.log("Waiting for client to connect socket "+port, null);
			clientSocket = serverSocket.accept();
			testExecutionManager.log("Client connected to socket "+port, null);
			writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	void clientDisconnected() {
		testExecutionManager.log("Client disconnected from socket "+port, null);
		writer = null;
		reader = null;
	}
	
	
	public void handleEvent(E pEvent) {
		eventQueue.add(pEvent);
	}
	
	public void publishToSocket(Message obj) {
		try {
			writer.println(gson.toJson(obj));
			writer.flush();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Message receiveFromSocket(Class clazz) {
		try {
			String newLine = reader.readLine();
			return (Message )gson.fromJson(newLine, clazz);
		} catch(Exception e) {
			throw new RuntimeException(e);
		} 
	}

	

	
	public E readFrom(BufferedReader reader, Class<E> clazz) throws IOException {
		String line = reader.readLine();
		return (E) gson.fromJson(line, clazz);
	}
	
	public void publishResponse(R pResponse) {
		try {
			testExecutionManager.log(gson.toJson(pResponse), null);
		} catch(Exception e) {
			testExecutionManager.log(e.getMessage(), null);
		}
	}

}
