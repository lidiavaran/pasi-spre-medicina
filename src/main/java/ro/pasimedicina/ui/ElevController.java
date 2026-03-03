package ro.pasimedicina.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ro.pasimedicina.bd.*;
import ro.pasimedicina.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller pentru gestionarea interfetei si actiunilor specifice elevului
 */
@Controller
@RequestMapping("/elev")
public class ElevController {

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    @Autowired
    private CursRepository cursRepository;

    @Autowired
    private LectieRepository lectieRepository;
    
    @Autowired
    private ProgresLectieRepository progresRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProgramStudiuRepository programRepo;

    @Autowired
    private ProgramareRepository programareRepo;

    /**
     * Afiseaza panoul principal al elevului cu progresul cursurilor si planul de studiu
     */
    @GetMapping("/dashboard/{id}")
    public String dashboardElev(@PathVariable Long id, Model model) {
        Utilizator elev = utilizatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Elevul nu a fost gasit"));
        
        Map<Long, Integer> procenteCursuri = new HashMap<>();
        List<ProgresLectie> toateProgresele = progresRepo.findByElev(elev);

        for (Curs curs : elev.getCursuri()) {
            int totalLectiiInCurs = (curs.getLectii() != null) ? curs.getLectii().size() : 0;

            long finalizate = toateProgresele.stream()
                    .filter(p -> p.getLectie().getCurs().getId().equals(curs.getId()))
                    .filter(p -> p.getProcentCompletat() == 100)
                    .count();

            int procentajReal = 0;
            if (totalLectiiInCurs > 0) {
                procentajReal = (int) (((double) finalizate / totalLectiiInCurs) * 100);
            }
            procenteCursuri.put(curs.getId(), procentajReal);
        }

        List<ProgramStudiu> programe = programRepo.findByElevOrderByDataLimitaAsc(elev);

        List<Programare> programari = programareRepo.findByElevId(id).stream()
                .filter(p -> p.getDataOra().isAfter(LocalDateTime.now().minusHours(2)))
                .sorted(Comparator.comparing(Programare::getDataOra))
                .collect(Collectors.toList());

        model.addAttribute("elev", elev);
        model.addAttribute("cursuri", elev.getCursuri());
        model.addAttribute("procente", procenteCursuri);
        model.addAttribute("programe", programe);
        model.addAttribute("programari", programari);
        
        return "elev-dashboard";
    }

    /**
     * Afiseaza lectiile asignate dintr un curs specific pentru elevul logat
     */
    @GetMapping("/curs/{cursId}")
    public String vizualizareCurs(@PathVariable Long cursId, @RequestParam Long elevId, Model model) {
        Curs curs = cursRepository.findById(cursId).orElseThrow();
        Utilizator elev = utilizatorRepository.findById(elevId).orElseThrow();

        List<ProgresLectie> progrese = progresRepo.findByElev(elev).stream()
                .filter(p -> p.getLectie().getCurs().getId().equals(cursId))
                .collect(Collectors.toList());
        
        List<Long> lectiiFinalizateIds = progrese.stream()
                .filter(p -> p.getProcentCompletat() == 100)
                .map(p -> p.getLectie().getId())
                .collect(Collectors.toList());

        model.addAttribute("curs", curs);
        model.addAttribute("lectiiAsignate", progrese.stream().map(ProgresLectie::getLectie).collect(Collectors.toList()));
        model.addAttribute("terminate", lectiiFinalizateIds);
        model.addAttribute("elev", elev);

        return "aula-curs";
    }
    
    /**
     * Permite vizualizarea continutului unei lectii si stadiul progresului
     */
    @GetMapping("/lectie/{id}")
    public String vizualizareLectie(@PathVariable Long id, @RequestParam Long elevId, Model model) {
        Lectie lectie = lectieRepository.findById(id).orElseThrow();
        Utilizator elev = utilizatorRepository.findById(elevId).orElseThrow();
        ProgresLectie progres = progresRepo.findByElevAndLectie(elev, lectie).orElse(null);

        model.addAttribute("lectie", lectie);
        model.addAttribute("elev", elev); 
        model.addAttribute("progres", progres);
        
        return "elev-lectie";
    }

    /**
     * Marcheaza o lectie ca fiind finalizata si actualizeaza data terminarii
     */
    @PostMapping("/lectie/finalizeaza")
    public String finalizeazaLectie(@RequestParam Long lectieId, @RequestParam Long elevId) {
        Utilizator elev = utilizatorRepository.findById(elevId).orElseThrow();
        Lectie lectie = lectieRepository.findById(lectieId).orElseThrow();
        
        progresRepo.findByElevAndLectie(elev, lectie).ifPresent(p -> {
            p.setProcentCompletat(100);
            p.setDataFinalizarii(LocalDateTime.now());
            progresRepo.save(p);
        });
        
        return "redirect:/elev/curs/" + lectie.getCurs().getId() + "?elevId=" + elevId;
    }

    /**
     * Permite descarcarea fisierului atasat unei lectii din baza de date
     */
    @GetMapping("/lectie/descarca/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> descarcaFisier(@PathVariable Long id) {
        Lectie lectie = lectieRepository.findById(id).orElseThrow();
        if (lectie.getDateFisier() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + lectie.getNumeFisier() + "\"")
                .contentType(MediaType.parseMediaType(lectie.getTipFisier()))
                .body(lectie.getDateFisier());
    }

    /**
     * Afiseaza pagina cu datele de profil ale utilizatorului
     */
    @GetMapping("/profil/{id}")
    public String paginaProfil(@PathVariable Long id, Model model) {
        Utilizator elev = utilizatorRepository.findById(id).orElseThrow();
        model.addAttribute("user", elev);
        return "profil";
    }

    /**
     * Proceseaza cererea de actualizare a parolei pentru securitatea contului
     */
    @PostMapping("/profil/schimba-parola")
    public String schimbaParola(@RequestParam Long id, @RequestParam String parolaNoua) {
        return proceseazaSchimbareParola(id, parolaNoua);
    }

    /**
     * Metoda alternativa pentru actualizarea parolei din interfata
     */
    @PostMapping("/profil/update-parola")
    public String updateParola(@RequestParam Long id, @RequestParam String parolaNoua) {
        return proceseazaSchimbareParola(id, parolaNoua);
    }

    /**
     * Logica interna pentru criptarea si salvarea parolei noi
     */
    private String proceseazaSchimbareParola(Long id, String parolaNoua) {
        Utilizator elev = utilizatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Elevul nu a fost gasit"));
        elev.setParola(passwordEncoder.encode(parolaNoua));
        utilizatorRepository.save(elev);
        return "redirect:/elev/profil/" + id + "?success=true";
    }

    /**
     * Permite elevului sa incarce un fisier ca raspuns pentru tema primita
     */
    @PostMapping("/program-studiu/incarca-raspuns")
    public String incarcaRaspuns(@RequestParam Long planId, @RequestParam("fisierTema") MultipartFile fisier) throws IOException {
        ProgramStudiu plan = programRepo.findById(planId).orElseThrow();
        if (fisier != null && !fisier.isEmpty()) {
            plan.setNumeFisierRaspuns(fisier.getOriginalFilename());
            plan.setTipFisierRaspuns(fisier.getContentType());
            plan.setDateFisierRaspuns(fisier.getBytes());
            programRepo.save(plan);
        }
        return "redirect:/elev/dashboard/" + plan.getElev().getId();
    }
    
    /**
     * Permite descarcarea materialului suport oferit de mentor pentru studiu
     */
    @GetMapping("/program-studiu/descarca-material/{id}")
    public ResponseEntity<byte[]> descarcaMaterialMentor(@PathVariable Long id) {
        ProgramStudiu p = programRepo.findById(id).orElseThrow();
        if (p.getDateFisier() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + p.getNumeFisier() + "\"")
                .contentType(MediaType.parseMediaType(p.getTipFisier()))
                .body(p.getDateFisier());
    }
}