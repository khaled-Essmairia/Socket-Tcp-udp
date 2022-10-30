package com.tekcreek.socketprg.groupchat;

import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JOptionPane;

import com.tekcreek.socketprg.groupchat.client.core.BroadcastServerProxy;
import com.tekcreek.socketprg.groupchat.client.view.ChatFrame;

public class Client {

	public static void main(String[] args) throws Exception {
		DatagramSocket clientSocket = new DatagramSocket();
		
		String userName = JOptionPane.showInputDialog("Enter your name");
		
		BroadcastServerProxy broadcastServerProxy = 
				new BroadcastServerProxy(
						InetAddress.getLocalHost(),
						9000, 
						clientSocket, 
						userName);
		
		broadcastServerProxy.join();
		
		ChatFrame frame = new ChatFrame(userName, broadcastServerProxy);
		frame.setVisible(true);
	}

}
