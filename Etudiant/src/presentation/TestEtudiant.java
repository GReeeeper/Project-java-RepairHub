package presentation;

import java.util.Optional;
import java.util.OptionalDouble;

import dao.Etudiant;
import metier.GestionEtudiant;

public class TestEtudiant {
	public static void main(String[] args) {
		Etudiant e1 = Etudiant.builder()
							  .id(1)
							  .nom("7amid")
							  .prenom("Mohammed 7amid")
							  .age(21)
							  .ville("Anfa place")
							  .moyennegen(2).build();
		Etudiant e2 = Etudiant.builder()
				  .id(2)
				  .nom("Omar")
				  .prenom("Mohammed 7amid")
				  .age(24)
				  .ville("Sidi slimane")
				  .moyennegen(12).build();
		Etudiant e3 = Etudiant.builder()
				  .id(3)
				  .nom("Moha")
				  .prenom("O7ammo zayani")
				  .age(48)
				  .ville("sla 9dima")
				  .moyennegen(5).build();
		Etudiant e4 = Etudiant.builder()
				  .id(4)
				  .nom("Roudani")
				  .prenom("Mohammed amine")
				  .age(32)
				  .ville("Rabat 9dima")
				  .moyennegen(10).build();
		Etudiant e5 = Etudiant.builder()
				  .id(5)
				  .nom("simo")
				  .prenom("llah ynsro")
				  .age(62)
				  .ville("maroc")
				  .moyennegen(20).build();
		Etudiant e6 = Etudiant.builder()
				  .id(6)
				  .nom("wld simo")
				  .prenom("3")
				  .age(22)
				  .ville("morocco")
				  .moyennegen(40).build();
		Etudiant e7 = Etudiant.builder()
				  .id(7)
				  .nom("Zait")
				  .prenom("Mzeyt")
				  .age(22)
				  .ville("Sidi kacem")
				  .moyennegen(14).build();
		Etudiant e8 = Etudiant.builder()
				  .id(8)
				  .nom("Arous")
				  .prenom("Soura")
				  .age(12)
				  .ville("Rabat place")
				  .moyennegen(11).build();
		Etudiant e9 = Etudiant.builder()
				  .id(9)
				  .nom("7amid")
				  .prenom("Mohammed 7amid")
				  .age(21)
				  .ville("Anfa place")
				  .moyennegen(21).build();
		Etudiant e10 = Etudiant.builder()
				  .id(10)
				  .nom("7amid")
				  .prenom("Walad")
				  .age(21)
				  .ville("Anfa place")
				  .moyennegen(12).build();
	GestionEtudiant ge = new GestionEtudiant();
	ge.ajouter(e1);
	ge.ajouter(e2);
	ge.ajouter(e3);
	ge.ajouter(e4);
	ge.ajouter(e5);
	ge.ajouter(e6);
	ge.ajouter(e7);
	ge.ajouter(e8);
	ge.ajouter(e9);
	ge.ajouter(e10);
	
	ge.lister().stream().forEach(System.out::println);
	System.out.println("=================================");
	ge.lister().stream().filter(x -> x.getMoyennegen()>=15).forEach(System.out::println);
	System.out.println("=================================");
	OptionalDouble moyenne=ge.lister().stream().mapToDouble(x->x.getMoyennegen()).average();
	System.out.println("la moyenne est : "+moyenne);
	
	
	}
	
	
}
