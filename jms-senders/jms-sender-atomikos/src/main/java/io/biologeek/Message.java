package io.biologeek;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Message {

	@Id
	private Long id;
	private String text;

	public Message() {
		// TODO Auto-generated constructor stub
	}

	public Message(Long id, String string) {
		this.text = string;
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
