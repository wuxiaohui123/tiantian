package com.yinhai.ta3.system.org.domain;


import java.io.Serializable;

import com.yinhai.ta3.system.sysapp.domain.Menu;

public class PositionAuthrityId implements Serializable {

	private Position taposition;
	private Menu tamenu;

	public PositionAuthrityId() {
	}

	public PositionAuthrityId(Position taposition, Menu tamenu) {
		this.taposition = taposition;
		this.tamenu = tamenu;
	}

	public Position getTaposition() {
		return taposition;
	}

	public void setTaposition(Position taposition) {
		this.taposition = taposition;
	}

	public Menu getTamenu() {
		return tamenu;
	}

	public void setTamenu(Menu tamenu) {
		this.tamenu = tamenu;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof PositionAuthrityId))
			return false;
		PositionAuthrityId castOther = (PositionAuthrityId) other;

		return ((getTaposition() == castOther.getTaposition()) || ((getTaposition() != null)
				&& (castOther.getTaposition() != null) && (getTaposition().equals(castOther.getTaposition()))))
				&& ((getTamenu() == castOther.getTamenu()) || ((getTamenu() != null) && (castOther.getTamenu() != null) && (getTamenu()
						.equals(castOther.getTamenu()))));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getTaposition() == null ? 0 : getTaposition().hashCode());
		result = 37 * result + (getTamenu() == null ? 0 : getTamenu().hashCode());
		return result;
	}
}
