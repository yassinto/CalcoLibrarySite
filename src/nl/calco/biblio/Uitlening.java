package nl.calco.biblio;

import java.io.Serializable;
import java.sql.Date;

public class Uitlening implements Serializable { // Lending-out of copy class

	// Attributes
	private Medewerker medewerker; 	// Employee object to whom copy is lent out to
	private Date datumUitleen; 		// Date copy was lend out on
	private int exemplaarId; 		// Unique key of copy

	// Getters and Setters
	public Medewerker getMedewerker() {
		return medewerker;
	}

	public void setMedewerker(Medewerker medewerker) {
		this.medewerker = medewerker;
	}

	public Date getDatumUitleen() {
		return datumUitleen;
	}

	public void setDatumUitleen(Date datumUitleen) {
		this.datumUitleen = datumUitleen;
	}

	public int getExemplaarId() {
		return exemplaarId;
	}

	public void setExemplaarId(int exemplaarId) {
		this.exemplaarId = exemplaarId;
	}

}
