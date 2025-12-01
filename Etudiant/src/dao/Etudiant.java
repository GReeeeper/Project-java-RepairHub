package dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etudiant {
	private int id;
	private String nom;
	private String prenom;
	private String ville;
	private int age;
	private double moyennegen;

}
