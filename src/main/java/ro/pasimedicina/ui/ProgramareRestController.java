package ro.pasimedicina.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.pasimedicina.model.Programare;
import ro.pasimedicina.servicii.ProgramareServiciu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

/**
 * Controller de tip REST pentru furnizarea datelor necesare calendarului de programari
 */
@RestController
@RequestMapping("/admin/api")
public class ProgramareRestController {

    @Autowired
    private ProgramareServiciu programareServiciu;

    /**
     * Extrage lista de programari viitoare si le formateaza ca obiecte JSON pentru calendar
     */
    @GetMapping("/programari")
    public List<Map<String, Object>> getProgramariPentruCalendar() {
        List<Programare> programari = programareServiciu.toateProgramarile();
        List<Map<String, Object>> events = new ArrayList<>();
        
        LocalDateTime acum = LocalDateTime.now();

        for (Programare p : programari) {
            if (p.getDataOra() != null && p.getDataOra().isAfter(acum)) {
                Map<String, Object> event = new HashMap<>();
                event.put("id", p.getId());
                event.put("title", p.getSubiect() + " - " + (p.getElev() != null ? p.getElev().getNume() : "Elev"));
                event.put("start", p.getDataOra());
                event.put("url", p.getLinkMeeting());
                
                events.add(event);
            }
        }
        return events;
    }
}