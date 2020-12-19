package com.alvin.geekchat.model;

public class User implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private final int id;
	private final String name;
	
	public User(int id, String name) {
		this.id = id;
		this.name = new String(name);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (! (o instanceof User)) {
			return false;
		}
		if (((User)o).id == this.id) {
			return true;
		} else {
			return false;
		}
	}
}
