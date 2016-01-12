package com.cchat.service;

import com.cchat.service.Person;
import com.cchat.common.base.data.ChatMessage;

interface IConnectServiceCallback{
	void receive(in ChatMessage chatMessage);
	void loginState(boolean isState);
//	double getWeight();
	void rosterStateChange(in Person person, int type);
	void passPersons(in List<Person> persons);
	
}