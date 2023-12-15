package br.com.aws.client.dto;

import br.com.aws.client.enuns.EventTypeEnum;

public class EnvelopeDTO {
	
	private EventTypeEnum eventType;
	
	private String data;

	public EventTypeEnum getEventType() {
		return eventType;
	}

	public void setEventType(EventTypeEnum eventType) {
		this.eventType = eventType;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
