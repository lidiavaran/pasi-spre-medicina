package ro.pasimedicina.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Entitate care reprezinta un curs in sistem si relatiile acestuia cu mentorii si elevii
 */
@Entity
@Table(name = "cursuri")
public class Curs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titlu;
    private String materie;
    private String manual;
    
    @Column(columnDefinition = "TEXT")
    private String descriere;
    
    /**
     * Relatie de tip Many To Many intre cursuri si utilizatorii cu rol de mentor
     */
    @ManyToMany
    @JoinTable(
      name = "curs_mentori",
      joinColumns = @JoinColumn(name = "curs_id"),
      inverseJoinColumns = @JoinColumn(name = "utilizator_id")
    )
    private List<Utilizator> mentori = new ArrayList<>();
    
    /**
     * Lista lectiilor continute de acest curs cu stergere in cascada
     */
    @OneToMany(mappedBy = "curs", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Lectie> lectii = new ArrayList<>();
    
    /**
     * Lista elevilor inscrisi la acest curs
     */
    @ManyToMany
    @JoinTable(
      name = "inscrieri",
      joinColumns = @JoinColumn(name = "curs_id"),
      inverseJoinColumns = @JoinColumn(name = "utilizator_id")
    )
    private List<Utilizator> elevi = new ArrayList<>();

    public Curs() {}

    public Curs(String titlu, String materie, String manual, String descriere) {
        this.titlu = titlu;
        this.materie = materie;
        this.manual = manual;
        this.descriere = descriere;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }
    
    public String getMaterie() { return materie; }
    public void setMaterie(String materie) { this.materie = materie; }
    
    public String getManual() { return manual; }
    public void setManual(String manual) { this.manual = manual; }
    
    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
    
    public List<Utilizator> getMentori() { return mentori; }
    public void setMentori(List<Utilizator> mentori) { this.mentori = mentori; }
    
    public List<Lectie> getLectii() { return lectii; }
    public void setLectii(List<Lectie> lectii) { this.lectii = lectii; }
    
    public List<Utilizator> getElevi() { return elevi; }
    public void setElevi(List<Utilizator> elevi) { this.elevi = elevi; }
}