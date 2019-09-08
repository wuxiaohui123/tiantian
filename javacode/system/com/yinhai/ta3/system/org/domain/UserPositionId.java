package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class UserPositionId implements Serializable {

	private User tauser;
	private Position taposition;

	public UserPositionId() {
	}

	public UserPositionId(User tauser, Position taposition) {
		this.tauser = tauser;
		this.taposition = taposition;
	}

	public User getTauser() {
		return tauser;
	}

	public void setTauser(User tauser) {
		this.tauser = tauser;
	}

	public Position getTaposition() {
		return taposition;
	}

	public void setTaposition(Position taposition) {
		this.taposition = taposition;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof UserPositionId))
			return false;
		UserPositionId castOther = (UserPositionId) other;

		return ((getTauser() == castOther.getTauser()) || ((getTauser() != null) && (castOther.getTauser() != null) && (getTauser()
				.equals(castOther.getTauser()))))
				&& ((getTaposition() == castOther.getTaposition()) || ((getTaposition() != null)
						&& (castOther.getTaposition() != null) && (getTaposition().equals(castOther.getTaposition()))));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getTauser() == null ? 0 : getTauser().hashCode());
		result = 37 * result + (getTaposition() == null ? 0 : getTaposition().hashCode());
		return result;
	}
}
