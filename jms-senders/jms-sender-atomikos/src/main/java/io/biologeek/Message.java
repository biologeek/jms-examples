package io.biologeek;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Message {

	@Id
	private Long id;
	private String text;
	/**
	 * 0 : unread
	 * 1 : read
	 */
	private Integer status;

	public Message() {
		// TODO Auto-generated constructor stub
	}

	public Message(Long id, String string) {
		this.text = string;
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
