package ro.pasimedicina.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ro.pasimedicina.bd.*;
import ro.pasimedicina.model.*;
import ro.pasimedicina.servicii.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate; 
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller pentru gestionarea activitatilor si interactiunilor specifice mentorului
 */
@Controller
@RequestMapping("/mentor")
public class MentorController {

    @Autowired private CursRepository cursRepository;
    @Autowired private LectieRepository lectieRepository;
    @Autowired private ProgramareRepository programareRepo;
    @Autowired private UtilizatorServiciu utilizatorServiciu;
    @Autowired private ProgresLectieRepository progresRepository;
    @Autowired private UtilizatorRepository utilizatorRepository;
    @Autowired private ProgramStudiuRepository programStudiuRepo;

    /**
     * Afiseaza panoul principal al mentorului cu statistici si programari
     */
    @GetMapping("/dashboard")
    public String dashboardMentor(Model model, Principal principal) {
        String emailLogat = principal.getName();
        Utilizator mentor = utilizatorRepository.findByEmail(emailLogat)
                .orElseThrow(() -> new RuntimeException("Mentorul nu a fost gasit"));
        model.addAttribute("mentor", mentor);
        model.addAttribute("cursuri", cursRepository.findAll()); 
        model.addAttribute("programariAzi", programareRepo.findByMentorId(mentor.getId()));
        model.addAttribute("elevi", utilizatorRepository.findByRolAndMentor("ELEV", mentor));
        return "mentor-dashboard";
    }

    /**
     * Afiseaza orarul complet sub forma de cronologie pentru mentor
     */
    @GetMapping("/orar")
    public String vizualizareOrar(Model model, Principal principal) {
        Utilizator mentor = utilizatorRepository.findByEmail(principal.getName()).orElseThrow();
        
        List<Programare> toateProgramarile = programareRepo.findByMentorId(mentor.getId()).stream()
                .sorted(Comparator.comparing(Programare::getDataOra))
                .collect(Collectors.toList());
        
        model.addAttribute("mentor", mentor);
        model.addAttribute("programari", toateProgramarile);
        return "mentor-orar";
    }

    /**
     * Vizualizeaza progresul detaliat si lectiile asignate unui anumit elev
     */
    @GetMapping("/elev/detalii/{id}")
    public String detaliiElev(@PathVariable Long id, Model model, Principal principal) {
        Utilizator mentor = utilizatorRepository.findByEmail(principal.getName()).orElseThrow();
        Utilizator elev = utilizatorServiciu.gasesteDupaId(id);
        if (elev == null || elev.getMentor() == null || !elev.getMentor().getId().equals(mentor.getId())) {
            return "redirect:/mentor/sala-de-clasa";
        }
        Map<Long, Integer> procenteCursuri = new HashMap<>();
        List<ProgresLectie> toateProgresele = progresRepository.findByElev(elev);
        for (Curs curs : elev.getCursuri()) {
            int totalLectiiInCurs = curs.getLectii().size();
            long finalizate = toateProgresele.stream()
                    .filter(p -> p.getLectie().getCurs().getId().equals(curs.getId()))
                    .filter(p -> p.getProcentCompletat() == 100)
                    .count();
            int procentajReal = (totalLectiiInCurs > 0) ? (int) (((double) finalizate / totalLectiiInCurs) * 100) : 0;
            procenteCursuri.put(curs.getId(), procentajReal);
        }
        model.addAttribute("elev", elev);
        model.addAttribute("procenteCursuri", procenteCursuri);
        model.addAttribute("toateLectiile", lectieRepository.findAll());
        model.addAttribute("progrese", toateProgresele);
        model.addAttribute("programeSetate", programStudiuRepo.findByElevOrderByDataLimitaAsc(elev));
        return "mentor-detalii-elev";
    }
 

    /**
     * Afiseaza lista programarilor viitoare ale mentorului
     */
    @GetMapping("/programari")
    public String paginaProgramari(Model model, Principal principal) {
        Utilizator mentor = utilizatorRepository.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("mentor", mentor);
        model.addAttribute("elevi", utilizatorRepository.findByRolAndMentor("ELEV", mentor));
        model.addAttribute("programari", programareRepo.findByMentorId(mentor.getId()).stream()
                .filter(p -> p.getDataOra().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Programare::getDataOra))
                .collect(Collectors.toList()));
        return "mentor-programari";
    }

    /**
     * Salveaza o intalnire noua intre mentor si elev
     */
    @PostMapping("/programare/noua")
    public String creeazaProgramare(@RequestParam Long elevId,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dataOra,
                                    @RequestParam(required = false) String subiect,
                                    @RequestParam(required = false) String linkMeeting,
                                    Principal principal) {
        Utilizator mentor = utilizatorRepository.findByEmail(principal.getName()).orElseThrow();
        Utilizator elev = utilizatorRepository.findById(elevId).orElseThrow();
        Programare p = new Programare();
        p.setMentor(mentor);
        p.setElev(elev);
        p.setSubiect(subiect);
        p.setDataOra(dataOra);
        p.setLinkMeeting(linkMeeting);
        programareRepo.save(p);
        return "redirect:/mentor/dashboard";
    }

    /**
     * Listeaza toate cursurile inregistrate in platforma
     */
    @GetMapping("/cursuri")
    public String toateCursurile(Model model) {
        model.addAttribute("listaCursuri", cursRepository.findAll());
        return "mentor-cursuri";
    }

    /**
     * Vizualizeaza structura si lectiile unui curs ales
     */
    @GetMapping("/cursuri/detalii/{id}")
    public String detaliiCurs(@PathVariable Long id, Model model) {
        Curs curs = cursRepository.findById(id).orElseThrow();
        model.addAttribute("curs", curs);
        return "mentor-detalii-curs";
    }

    /**
     * Afiseaza informatii despre o lectie specifica
     */
    @GetMapping("/lectii/detalii/{id}")
    public String detaliiLectie(@PathVariable Long id, Model model) {
        Lectie lectie = lectieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lectia nu a fost gasita"));
        model.addAttribute("lectie", lectie);
        return "mentor-detalii-lectie";
    }

    /**
     * Permite descarcarea materialelor didactice atasate lectiilor
     */
    @GetMapping("/lectii/download/{id}")
    public ResponseEntity<byte[]> descarcaMaterial(@PathVariable Long id) {
        Lectie lectie = lectieRepository.findById(id).orElseThrow();
        if (lectie.getDateFisier() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(lectie.getTipFisier()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + lectie.getNumeFisier() + "\"")
                .body(lectie.getDateFisier());
    }

    /**
     * Creeaza o lectie noua si proceseaza fisierul incarcat
     */
    @PostMapping("/lectii/salveaza")
    public String salveazaLectie(@RequestParam String titlu, 
                                 @RequestParam Long cursId, 
                                 @RequestParam("fisierIncarcat") MultipartFile fisier) throws IOException {
        Curs curs = cursRepository.findById(cursId).orElse(null);
        if (curs != null) {
            Lectie lectie = new Lectie();
            lectie.setTitlu(titlu);
            lectie.setCurs(curs);
            if (fisier != null && !fisier.isEmpty()) {
                lectie.setNumeFisier(fisier.getOriginalFilename());
                lectie.setTipFisier(fisier.getContentType());
                lectie.setDateFisier(fisier.getBytes());
            }
            lectieRepository.save(lectie);
        }
        return "redirect:/mentor/cursuri/detalii/" + cursId;
    }

    /**
     * Sterge o lectie din structura cursului
     */
    @GetMapping("/lectii/sterge/{id}")
    public String stergeLectie(@PathVariable Long id) {
        Lectie lectie = lectieRepository.findById(id).orElse(null);
        if (lectie != null) {
            Long cursId = lectie.getCurs().getId();
            lectieRepository.delete(lectie);
            return "redirect:/mentor/cursuri/detalii/" + cursId;
        }
        return "redirect:/mentor/cursuri";
    }

    /**
     * Afiseaza lista elevilor asignati mentorului logat
     */
    @GetMapping("/sala-de-clasa")
    public String salaDeClasa(Model model, Principal principal) {
        Utilizator mentor = utilizatorRepository.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("elevi", utilizatorRepository.findByRolAndMentor("ELEV", mentor));
        return "sala-de-clasa";
    }
    
    /**
     * Creeaza o legatura de progres intre un elev si o lectie specifica
     */
    @PostMapping("/elev/asigneaza-lectie")
    public String asigneazaLectie(@RequestParam Long elevId, @RequestParam Long lectieId) {
        Utilizator elev = utilizatorServiciu.gasesteDupaId(elevId);
        Lectie lectie = lectieRepository.findById(lectieId).orElse(null);
        if (elev != null && lectie != null) {
            if (progresRepository.findByElevAndLectie(elev, lectie).isEmpty()) {
                ProgresLectie p = new ProgresLectie();
                p.setElev(elev);
                p.setLectie(lectie);
                p.setProcentCompletat(0);
                progresRepository.save(p);
            }
        }
        return "redirect:/mentor/elev/detalii/" + elevId;
    }
    
    /**
     * Vizualizeaza toate programele de studiu active create de mentor
     */
    @GetMapping("/programe-studiu")
    public String toateProgrameleStudiu(Model model, Principal principal) {
        Utilizator mentor = utilizatorRepository.findByEmail(principal.getName()).orElseThrow();
        List<ProgramStudiu> programe = programStudiuRepo.findByMentorOrderByDataLimitaAsc(mentor);
        model.addAttribute("programe", programe);
        return "mentor-programe-studiu";
    }

    /**
     * Permite descarcarea sarcinii de lucru create de mentor
     */
    @GetMapping("/program-studiu/descarca/{id}")
    public ResponseEntity<byte[]> descarcaFisierPlan(@PathVariable Long id) {
        ProgramStudiu p = programStudiuRepo.findById(id).orElseThrow();
        if (p.getDateFisier() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + p.getNumeFisier() + "\"")
                .contentType(MediaType.parseMediaType(p.getTipFisier()))
                .body(p.getDateFisier());
    }

    /**
     * Permite descarcarea solutiei trimise de elev pentru un plan de studiu
     */
    @GetMapping("/program-studiu/descarca-raspuns/{id}")
    public ResponseEntity<byte[]> descarcaRaspunsElev(@PathVariable Long id) {
        ProgramStudiu p = programStudiuRepo.findById(id).orElseThrow();
        if (p.getDateFisierRaspuns() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"RASPUNS_" + p.getNumeFisierRaspuns() + "\"")
                .contentType(MediaType.parseMediaType(p.getTipFisierRaspuns()))
                .body(p.getDateFisierRaspuns());
    }
    
    /**
     * Salveaza un plan de studiu nou si ataseaza documentul sarcina
     */
    @PostMapping("/program-studiu/salveaza")
    public String salveazaProgramStudiu(@RequestParam("elevId") Long elevId,
                                        @RequestParam("titlu") String titlu,
                                        @RequestParam("descriere") String descriere,
                                        @RequestParam("dataLimita") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataLimita,
                                        @RequestParam("fisierSarcina") MultipartFile fisier,
                                        Principal principal) throws IOException {
        
        Utilizator mentor = utilizatorRepository.findByEmail(principal.getName()).orElseThrow();
        Utilizator elev = utilizatorRepository.findById(elevId).orElseThrow();

        ProgramStudiu program = new ProgramStudiu();
        program.setMentor(mentor);
        program.setElev(elev);
        program.setTitlu(titlu);
        program.setDescriere(descriere);
        program.setDataLimita(dataLimita);

        if (fisier != null && !fisier.isEmpty()) {
            program.setNumeFisier(fisier.getOriginalFilename());
            program.setTipFisier(fisier.getContentType());
            program.setDateFisier(fisier.getBytes());
        }
        
        programStudiuRepo.save(program);
        return "redirect:/mentor/elev/detalii/" + elevId;
    }
    
    /**
     * Sterge o programare existenta din sistem
     */
    @GetMapping("/programare/sterge/{id}")
    public String stergeProgramare(@PathVariable Long id, Principal principal) {
        Programare p = programareRepo.findById(id).orElseThrow();
        if (p.getMentor().getEmail().equals(principal.getName())) {
            programareRepo.delete(p);
        }
        return "redirect:/mentor/dashboard";
    }

    /**
     * Elimina un plan de studiu din baza de date
     */
    @GetMapping("/program-studiu/sterge/{id}")
    public String stergeProgramStudiu(@PathVariable Long id, Principal principal) {
        ProgramStudiu ps = programStudiuRepo.findById(id).orElseThrow();
        Long elevId = ps.getElev().getId();
        if (ps.getMentor().getEmail().equals(principal.getName())) {
            programStudiuRepo.delete(ps);
        }
        return "redirect:/mentor/elev/detalii/" + elevId;
    }
}