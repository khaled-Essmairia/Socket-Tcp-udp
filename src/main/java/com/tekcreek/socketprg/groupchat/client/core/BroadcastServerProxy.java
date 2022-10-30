package com.tekcreek.socketprg.groupchat.client.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.gson.Gson;
import com.tekcreek.socketprg.groupchat.commons.Command;
import com.tekcreek.socketprg.groupchat.commons.model.Message;
import com.tekcreek.socketprg.groupchat.commons.utils.ChatUtils;

public class BroadcastServerProxy {
	
	public final int MAX_DATA_SIZE = 2048;

	InetAddress serverAddress;
	int serverPort;
	DatagramSocket clientSocket;
	String userName;
	Gson gson = new Gson();
	ChatListener listener;
	ReceiverThread receiver;

	public BroadcastServerProxy(
						InetAddress serverAddress, 
						int serverPort, 
						DatagramSocket sock, 
						String name) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.clientSocket = sock;
		this.userName = name;
		receiver = new ReceiverThread();
		receiver.start();
	}
	
	public void setChatListener(ChatListener listener) {
		this.listener = listener;
	}

	public void join() {
		Message message = new Message();
		message.setCommand(Command.JOIN);
		message.setFrom(this.userName);
		String jsonMessage = gson.toJson(message);
		byte data[] = jsonMessage.getBytes();
		try {
			DatagramPacket packet = 
					new DatagramPacket(data, data.length, serverAddress, serverPort);
			this.clientSocket.send(packet);
		} catch (Exception e) {
		}
	}

	public void leave() {
		Message message = new Message();
		message.setCommand(Command.LEAVE);
		message.setFrom(this.userName);
		String jsonMessage = gson.toJson(message);
		byte data[] = jsonMessage.getBytes();
		try {
			DatagramPacket packet = 
					new DatagramPacket(data, data.length, serverAddress, serverPort);
			this.clientSocket.send(packet);
		} catch (Exception e) {
		}
		receiver.interrupt();
	}

	public void send(String strMessage) {
		Message message = new Message();
		message.setCommand(Command.MESSAGE);
		message.setFrom(this.userName);
		message.setMessage(strMessage);
		
		String jsonMessage = gson.toJson(message);
		
		byte data[] = jsonMessage.getBytes();
		try {
			DatagramPacket packet = 
					new DatagramPacket(data, data.length, serverAddress, serverPort);
			this.clientSocket.send(packet);
		} catch (Exception e) {
		}
	}
	
	private void processMessage(Message message) {
		if (listener == null) {
			return;
		}
		if (Command.JOIN == message.getCommand()) {
			listener.onJoin(message.getFrom());
		} else if (Command.LEAVE == message.getCommand()) {
			listener.onLeave(message.getFrom());
		} else {
			listener.onMessage(message.getFrom(), message.getMessage());
		}
	}
	
	class ReceiverThread extends Thread {
		public void run() {
			byte [] buffer = new byte[MAX_DATA_SIZE];
			while (!interrupted()) {
				try {
					DatagramPacket packet = new DatagramPacket(buffer, MAX_DATA_SIZE);
					clientSocket.receive(packet);
					String msgData = new String(packet.getData());
					System.out.println("Obtained message \n " + msgData);
					final Message message = gson.fromJson(msgData.trim(), Message.class);
					processMessage(message);
					ChatUtils.clearBuffer(buffer);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
