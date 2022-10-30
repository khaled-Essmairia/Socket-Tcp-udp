package com.tekcreek.socketprg.groupchat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.tekcreek.socketprg.groupchat.commons.model.Message;
import com.tekcreek.socketprg.groupchat.commons.utils.ChatUtils;
import com.tekcreek.socketprg.groupchat.server.core.MessageBroadcastService;

public class Server {
	
	public static final int MAX_DATA_SIZE = 2048;
	public static final int CONCURRENCY = 2;
	
	public static void main(String[] args) throws Exception {
		
		DatagramSocket serverSocket = new DatagramSocket(9000);
		ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENCY);

		final Gson gson = new Gson();
		final MessageBroadcastService broadcastService = 
							new MessageBroadcastService(serverSocket);
		
		
		byte [] buffer = new byte[MAX_DATA_SIZE];
		
		while(true) {
			final DatagramPacket packet = 
					new DatagramPacket(buffer, MAX_DATA_SIZE);
			
			System.out.println("Waiting from datagram");
			
			serverSocket.receive(packet);
			
			String msgData = new String(packet.getData());
			
			System.out.println("Obtained message \n " + msgData);
			
			final Message message = gson.fromJson(msgData.trim(), Message.class);
			
			executorService.execute(new Runnable() {
				public void run() {
					broadcastService.handleMessage(
							message, packet.getAddress(), packet.getPort());
				}
			});
			
			ChatUtils.clearBuffer(buffer);
		}
	}
}

/*
 * Message Format -
 * 
 * 1 - JOIN, 2 - MESSAGE, 3 - LEAVE
 * 	
 * {
 * 	 command: 1,
 *   from:"username",
 *   message:"message"
 * }
 * 		
 * */
