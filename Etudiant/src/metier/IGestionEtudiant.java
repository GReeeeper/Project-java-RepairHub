package metier;

import java.util.List;

import dao.Etudiant;
import exception.EtudiantNotFound;

public interface IGestionEtudiant {
	public void ajouter(Etudiant e);
	public Etudiant rechercher(int id) throws EtudiantNotFound;
	public void modifier(Etudiant e);
	public void supprimer(int id);
	public List<Etudiant>lister();
}
