package com.tekcreek.socketprg.groupchat.commons.model;

public class Message {
	int command;
	String from;
	String message;

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Message [command=" + command + ", from=" + from + ", message=" + message + "]";
	}
	
}
