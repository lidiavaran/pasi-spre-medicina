package ro.pasimedicina.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ro.pasimedicina.model.*;
import ro.pasimedicina.servicii.*;
import ro.pasimedicina.bd.*; 

import java.io.IOException;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

/**
 * Controller pentru gestionarea functiilor administrative din sistem
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UtilizatorServiciu utilizatorServiciu;
    @Autowired private UtilizatorRepository utilizatorRepository;
    @Autowired private CursServiciu cursServiciu;
    @Autowired private ProgramareServiciu programareServiciu;
    
    @Autowired private ProgramareRepository programareRepo;
    @Autowired private ProgramStudiuRepository programStudiuRepo;
    @Autowired private ProgresLectieRepository progresRepository;
    @Autowired private LectieRepository lectieRepository;

    /**
     * Afiseaza panoul principal cu statistici generale
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUtilizatori", utilizatorServiciu.totiUtilizatorii().size());
        model.addAttribute("totalCursuri", cursServiciu.toateCursurile().size());
        model.addAttribute("totalProgramari", programareServiciu.toateProgramarile().size());
        return "dashboard";
    }
    
    /**
     * Listeaza toti utilizatorii inregistrati in platforma
     */
    @GetMapping("/utilizatori")
    public String gestioneazaUtilizatori(Model model) {
        model.addAttribute("listaUtilizatori", utilizatorServiciu.totiUtilizatorii());
        return "utilizatori";
    }

    /**
     * Afiseaza formularul pentru crearea unui cont nou
     */
    @GetMapping("/utilizatori/nou")
    public String afiseazaPaginaInscriere(Model model) {
        model.addAttribute("utilizator", new Utilizator());
        model.addAttribute("listaCursuri", cursServiciu.toateCursurile()); 
        model.addAttribute("listaMentori", utilizatorServiciu.gasesteDupaRol("MENTOR"));
        return "admin-elev-nou";
    }

    /**
     * Salveaza un utilizator nou sau actualizat si asigneaza mentorul
     */
    @PostMapping("/utilizatori/salveaza")
    public String salveazaUtilizator(@ModelAttribute("utilizator") Utilizator u, 
                                     @RequestParam(value = "mentorId", required = false) Long mentorId) {
        if (u.getRol() == null || u.getRol().isEmpty()) {
            u.setRol("ELEV");
        }
        if (mentorId != null) {
            Utilizator mentor = utilizatorRepository.findById(mentorId).orElse(null);
            u.setMentor(mentor);
        }
        utilizatorServiciu.salveaza(u);
        return "redirect:/admin/utilizatori";
    }

    /**
     * Elimina definitiv un utilizator si curata toate datele asociate
     */
    @GetMapping("/utilizatori/sterge/{id}")
    public String stergeUtilizator(@PathVariable Long id) {
        Utilizator u = utilizatorRepository.findById(id).orElse(null);
        if (u != null) {
            programareRepo.deleteAll(programareRepo.findByElevId(u.getId()));
            programareRepo.deleteAll(programareRepo.findByMentorId(u.getId()));
            programStudiuRepo.deleteAll(programStudiuRepo.findByElevOrderByDataLimitaAsc(u));
            programStudiuRepo.deleteAll(programStudiuRepo.findByMentorOrderByDataLimitaAsc(u));
            progresRepository.deleteAll(progresRepository.findByElev(u));
            u.getCursuri().clear();
            utilizatorRepository.save(u);
            List<Utilizator> eleviSubMentor = utilizatorRepository.findByRolAndMentor("ELEV", u);
            for(Utilizator elev : eleviSubMentor) {
                elev.setMentor(null);
                utilizatorRepository.save(elev);
            }
            utilizatorRepository.delete(u);
        }
        return "redirect:/admin/utilizatori";
    }

    /**
     * Gestioneaza lista de cursuri si datele necesare pentru model
     */
    @GetMapping("/cursuri")
    public String gestioneazaCursuri(Model model) {
        model.addAttribute("listaCursuri", cursServiciu.toateCursurile());
        model.addAttribute("listaMentori", utilizatorServiciu.gasesteDupaRol("MENTOR"));
        model.addAttribute("listaElevi", utilizatorServiciu.gasesteDupaRol("ELEV"));
        return "admin-cursuri";
    }

    /**
     * Creeaza un curs nou cu titlu si descriere
     */
    @PostMapping("/cursuri/salveaza")
    public String salveazaCurs(@RequestParam String titlu, @RequestParam String descriere) {
        Curs curs = new Curs();
        curs.setTitlu(titlu);
        curs.setDescriere(descriere);
        cursServiciu.salveaza(curs);
        return "redirect:/admin/cursuri";
    }

    /**
     * Afiseaza informatii detaliate despre un curs anume
     */
    @GetMapping("/cursuri/detalii/{id}")
    public String detaliiCurs(@PathVariable Long id, Model model) {
        System.out.println("Caut cursul cu ID: " + id); // Vezi în consolă dacă ajunge aici
        Curs curs = cursServiciu.gasesteDupaId(id);
        
        if (curs == null) {
            System.out.println("Cursul nu a fost găsit!");
            return "redirect:/admin/cursuri";
        }
        
        model.addAttribute("curs", curs);
        return "detalii-curs"; 
    }

    /**
     * Aloca un mentor pentru un curs selectat
     */
    @PostMapping("/cursuri/asigneaza-mentor")
    public String asigneazaMentor(@RequestParam Long cursId, @RequestParam Long mentorId) {
        Curs curs = cursServiciu.gasesteDupaId(cursId);
        Utilizator mentor = utilizatorRepository.findById(mentorId).orElse(null);
        if (curs != null && mentor != null && !curs.getMentori().contains(mentor)) {
            curs.getMentori().add(mentor);
            cursServiciu.salveaza(curs);
        }
        return "redirect:/admin/cursuri";
    }

    /**
     * Inregistreaza un elev la un anumit curs
     */
    @PostMapping("/cursuri/inscrie-elev")
    public String inscrieElev(@RequestParam Long cursId, @RequestParam Long elevId) {
        Curs curs = cursServiciu.gasesteDupaId(cursId);
        Utilizator elev = utilizatorRepository.findById(elevId).orElse(null);
        if (curs != null && elev != null && !curs.getElevi().contains(elev)) {
            curs.getElevi().add(elev);
            cursServiciu.salveaza(curs);
        }
        return "redirect:/admin/cursuri";
    }

    /**
     * Sterge cursul selectat si elimina progresul lectiilor aferente
     */
    @GetMapping("/cursuri/sterge/{id}")
    public String stergeCurs(@PathVariable Long id) {
        Curs curs = cursServiciu.gasesteDupaId(id);
        if (curs != null) {
            List<ProgresLectie> progreseDeSters = progresRepository.findAll().stream()
                    .filter(p -> p.getLectie() != null && p.getLectie().getCurs() != null && p.getLectie().getCurs().getId().equals(id))
                    .toList();
            progresRepository.deleteAll(progreseDeSters);
            curs.getMentori().clear();
            curs.getElevi().clear();
            cursServiciu.salveaza(curs);
            cursServiciu.stergeCurs(id);
        }
        return "redirect:/admin/cursuri";
    }

    /**
     * Salveaza o lectie noua si proceseaza fisierul incarcat
     */
    @PostMapping("/lectii/salveaza")
    public String salveazaLectie(@RequestParam String titlu, @RequestParam Long cursId, 
                                 @RequestParam("fisierIncarcat") MultipartFile fisier) throws IOException {
        Curs curs = cursServiciu.gasesteDupaId(cursId);
        if (curs != null) {
            Lectie lectie = new Lectie();
            lectie.setTitlu(titlu);
            lectie.setCurs(curs);
            if (fisier != null && !fisier.isEmpty()) {
                lectie.setNumeFisier(fisier.getOriginalFilename());
                lectie.setTipFisier(fisier.getContentType());
                lectie.setDateFisier(fisier.getBytes());
            }
            cursServiciu.salveazaLectie(lectie);
        }
        return "redirect:/admin/cursuri";
    }

    /**
     * Elimina o lectie curatand mai intai progresul si legaturile cu cursul
     */
    @Transactional
    @GetMapping("/lectii/sterge/{id}")
    public String stergeLectie(@PathVariable Long id) {
        Lectie lectie = lectieRepository.findById(id).orElse(null);
        
        if (lectie != null) {
            List<ProgresLectie> progrese = progresRepository.findAll().stream()
                    .filter(p -> p.getLectie() != null && p.getLectie().getId().equals(id))
                    .toList();
            progresRepository.deleteAll(progrese);

            Curs curs = lectie.getCurs();
            if (curs != null) {
                curs.getLectii().remove(lectie);
                utilizatorRepository.flush(); 
            }

            lectieRepository.delete(lectie);
        }
        return "redirect:/admin/cursuri";
    }
    
    @GetMapping("/lectii/detalii/{id}")
    public String detaliiLectie(@PathVariable Long id, Model model) {
        Lectie lectie = lectieRepository.findById(id).orElse(null);
        if (lectie == null) return "redirect:/admin/cursuri";
        
        model.addAttribute("lectie", lectie);
        return "detalii-lectie"; 
    }
    
    @GetMapping("/lectii/descarca/{id}")
    public org.springframework.http.ResponseEntity<byte[]> descarcaFisier(@PathVariable Long id) {
        Lectie lectie = lectieRepository.findById(id).orElse(null);

        if (lectie == null || lectie.getDateFisier() == null) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }
        
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + lectie.getNumeFisier() + "\"")
                .contentType(org.springframework.http.MediaType.parseMediaType(lectie.getTipFisier()))
                .body(lectie.getDateFisier());
    }
    
    @GetMapping("/lectii/vizualizare/{id}")
    public String vizualizareLectie(@PathVariable Long id, Model model) {
        Lectie lectie = lectieRepository.findById(id).orElse(null);
        
        if (lectie == null) {
            return "redirect:/admin/cursuri";
        }
        model.addAttribute("lectie", lectie);
        return "detalii-lectie";
    }

    /**
     * Listeaza toate programarile viitoare in ordine cronologica
     */
    @GetMapping("/programari")
    public String gestioneazaProgramari(Model model) {
        List<Programare> viitoare = programareServiciu.toateProgramarile().stream()
                .filter(p -> p.getDataOra() != null && p.getDataOra().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Programare::getDataOra))
                .toList();
        model.addAttribute("listaElevi", utilizatorServiciu.gasesteDupaRol("ELEV"));
        model.addAttribute("listaMentori", utilizatorServiciu.gasesteDupaRol("MENTOR"));
        model.addAttribute("listaProgramari", viitoare);
        return "programari";
    }

    /**
     * Salveaza o programare noua intre un elev si un mentor
     */
    @PostMapping("/programari/salveaza")
    public String salveazaProgramare(@RequestParam Long elevId, @RequestParam Long mentorId,
                                     @RequestParam String subiect, 
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dataOra,
                                     @RequestParam String linkMeeting) {
        Utilizator elev = utilizatorRepository.findById(elevId).orElse(null);
        Utilizator mentor = utilizatorRepository.findById(mentorId).orElse(null);
        if (elev != null && mentor != null) {
            Programare p = new Programare();
            p.setElev(elev); p.setMentor(mentor); p.setSubiect(subiect);
            p.setDataOra(dataOra); p.setLinkMeeting(linkMeeting);
            programareServiciu.salveaza(p);
        }
        return "redirect:/admin/programari";
    }

    /**
     * Afiseaza orarul general cu toate sedintele stabilite
     */
    @GetMapping("/orar")
    public String afiseazaOrar(Model model) {
        model.addAttribute("programariOrar", programareServiciu.toateProgramarile());
        return "orar";
    }

    /**
     * Schimba rolul unui utilizator intre elev si mentor
     */
    @GetMapping("/utilizatori/schimba-rol/{id}")
    public String schimbaRol(@PathVariable Long id) {
        Utilizator u = utilizatorRepository.findById(id).orElse(null);
        if (u != null) {
            u.setRol("MENTOR".equals(u.getRol()) ? "ELEV" : "MENTOR");
            utilizatorRepository.save(u);
        }
        return "redirect:/admin/utilizatori";
    }
}