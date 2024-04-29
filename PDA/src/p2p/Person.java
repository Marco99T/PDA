package p2p;

import java.io.Serializable;

public class Person implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String nickname;
	private String message;
	
	public Person(String nickname, String message) {
		this.nickname = nickname;
		this.message = message;
	}

	public String getNickname() {
		return nickname;
	}

	public String getMessage() {
		return message;
	}
	
	

}
