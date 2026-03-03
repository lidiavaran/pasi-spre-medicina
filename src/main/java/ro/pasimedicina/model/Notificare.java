package ro.pasimedicina.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificari")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notificare {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String mesaj;
	
	private LocalDateTime dataCrearii;
	
	private boolean citita = false;
	
	@ManyToOne
	@JoinColumn(name = "utilizator_id")
	private Utilizator destinatar;

}
