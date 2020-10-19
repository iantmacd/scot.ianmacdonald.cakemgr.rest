package scot.ianmacdonald.cakemgr.rest.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonAlias;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = "ID"), @UniqueConstraint(columnNames = "TITLE") })
public class Cake implements Serializable {
	
	private static final long serialVersionUID = -1066651494071978589L;

//	Cake() {
//	}
//	
//	public Cake(String title, String description, String image) {
//		super();
//		this.title = title;
//		this.description = description;
//		this.image = image;
//	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String title;

	@JsonAlias({ "desc" })
	private String description;

	private String image;
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getImage() {
		return this.image;
	}

	@Override
	public String toString() {
		return "Cake [id=" + id + ", title=" + title + ", description=" + description + ", image=" + image + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, image, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Cake other = (Cake) obj;
		return Objects.equals(id, other.id) && Objects.equals(title, other.title)
				&& Objects.equals(description, other.description) && Objects.equals(image, other.image);
	}

}