package nl.calco.biblio;

import java.io.Serializable;
import java.sql.Date;

public class BoekBezit implements Serializable { // Class to create object of a
													// book/copy in possession
													// of an employee

	// Initially no objects of this class exist if the copy was in possession in the past
	// As a copy is returned through the interface a returnDate is added - in
	// that case an object of a past possession exists temporarly

	// Attributes retrievable/insertable from database
	private int exemplaarId; 		// Unique key attribute of copy
	private String boekNummer; 		// bookNumber
	private int boekVolg; 			// copyNumber
	private String titel; 			// title
	private Date datumUitleen; 		// loanDate
	private Date datumInleveren; 	// returnDate

	private boolean ingeleverd; 	// Boolean verified when book is returned

	// Getters & Setters
	public String getBoekNummer() {
		return boekNummer;
	}

	public void setBoekNummer(String boekNummer) {
		this.boekNummer = boekNummer;
	}

	public int getBoekVolg() {
		return boekVolg;
	}

	public void setBoekVolg(int boekVolg) {
		this.boekVolg = boekVolg;
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
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

	public Date getDatumInleveren() {
		return datumInleveren;
	}

	public void setDatumInleveren(Date datumInleveren) {
		this.datumInleveren = datumInleveren;
		// Boolean 'returned' linked to presence of returnDate
		if (datumInleveren == null) {
			setIngeleverd(false);
		} else {
			setIngeleverd(true);
		}
	}

	public boolean isIngeleverd() {
		return ingeleverd;
	}

	public void setIngeleverd(boolean ingeleverd) {
		this.ingeleverd = ingeleverd;
	}

}
