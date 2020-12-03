package nl.calco.biblio;

import java.io.Serializable;

public class Exemplaar implements Serializable { // Copy (of a book) class

	// Attributes as found in database
	private int exemplaarId; 			// Unique key attribute
	private int boekId; 				// Book id it is linked to
	private int exemplaarVolg; 			// Copy number
	private boolean vermist; 			// Whether copy is missing
	private Uitlening huidigeUitlening; // If copy is currently lent out it is linked to a lending object
	private boolean uitgeleen; 			// Whether copy is lent out

	// Getters & Setters
	public int getExemplaarId() {
		return exemplaarId;
	}

	public void setExemplaarId(int exemplaarId) {
		this.exemplaarId = exemplaarId;
	}

	public int getBoekId() {
		return boekId;
	}

	public void setBoekId(int boekId) {
		this.boekId = boekId;
	}

	public int getExemplaarVolg() {
		return exemplaarVolg;
	}

	public void setExemplaarVolg(int exemplaarVolg) {
		this.exemplaarVolg = exemplaarVolg;
	}

	public boolean isVermist() {
		return vermist;
	}

	public void setVermist(boolean vermist) {
		this.vermist = vermist;
	}

	public Uitlening getHuidigeUitlening() {
		return huidigeUitlening;
	}

	public void setHuidigeUitlening(Uitlening huidigeUitlening) {
		this.huidigeUitlening = huidigeUitlening;
		// Boolean 'lentOut' linked to presence of currentLending
		if (huidigeUitlening.getDatumUitleen() != null) {
			setUitgeleen(true);
		}
	}

	public boolean isUitgeleen() {
		return uitgeleen;
	}

	public void setUitgeleen(boolean uitgeleen) {
		this.uitgeleen = uitgeleen;
	}

}
