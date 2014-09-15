package example.swa.yesnogame.domain.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

/**
 * Entity. Simple type. Immtuable type. Clonable. Comparable. Equals on "title"
 * (key).
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class PollSimpleDto {

	Long Id;
	String Created;
	boolean IsOpen;
	String Owner;
	String Question;
	String Title;

	private static SimpleDateFormat sdfLong = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public PollSimpleDto(Long id, Long created, boolean isOpen, String owner, String question, String title) {
		super();
		this.Id = id;
		// e.g. "2014-09-03T20:40:55.496+00:00";
		this.Created = getCreatedStr(created);
		this.IsOpen = isOpen;
		this.Owner = owner;
		this.Question = question;
		this.Title = title;
	}

	public String getCreated() {
		return this.Created;
	}

	private String getCreatedStr(Long created) {
		Date date = new Date(created * 1000);
		String ret = sdfLong.format(date);
		return ret;
	}

	public Long getCreatedTime() {
		Date ret = null;
		try {
			ret = sdfLong.parse(this.Created);
		} catch (ParseException e) {
			try {
				ret = sdfShort.parse(this.Created);
			} catch (ParseException ex) {
				Log.e("DateTime", ex.toString());
			}
		}
		return ret.getTime() / 1000;
	}

	public Long getId() {
		return this.Id;
	}

	public String getOwner() {
		return this.Owner;
	}

	public String getQuestion() {
		return this.Question;
	}

	public String getTitle() {
		return this.Title;
	}

	public boolean isIsOpen() {
		return this.IsOpen;
	}

	public void setCreated(String created) {
		this.Created = created;
	}

	public void setId(Long id) {
		this.Id = id;
	}

	public void setIsOpen(boolean isOpen) {
		this.IsOpen = isOpen;
	}

	public void setOwner(String owner) {
		this.Owner = owner;
	}

	public void setQuestion(String question) {
		this.Question = question;
	}

	public void setTitle(String title) {
		this.Title = title;
	}

}
