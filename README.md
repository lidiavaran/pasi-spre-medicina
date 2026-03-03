# 🩺 Pași Spre Medicină - Sistem de Gestiune a Meditațiilor

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)](https://www.mysql.com/)

**Pași Spre Medicină** este o platformă web concepută pentru a digitaliza procesul de pregătire pentru admiterea la medicină. Aplicația conectează mentorii cu elevii, oferind un flux de lucru organizat pentru lecții, materiale și programări.

---

## 🚀 Funcționalități Principale

### 🔑 Securitate și Acces (RBAC)
* **Roluri Multiple:** Administrare diferențiată pentru **Admin**, **Mentor** și **Elev**.
* **Spring Security:** Protecție la nivel de endpoint și criptare a parolelor cu **BCrypt**.
* **Redirecționare Dinamică:** Utilizatorii sunt trimiși automat către dashboard-ul specific rolului lor după autentificare.

### 📚 Management Educațional
* **Structură Cursuri:** Organizare ierarhică a conținutului (Lecții -> Resurse).
* **Documente (LOB):** Sistem de încărcare și descărcare a materialelor de studiu (PDF/Imagini).
* **Progres:** Vizualizarea statusului ședințelor și a temelor parcurse.

### 📅 Planificare & API Intern
* **Calendar Interactiv:** Integrare cu **FullCalendar API** pentru gestionarea programărilor.
* **REST API:** Comunicare asincronă între Frontend și Backend pentru livrarea datelor în format JSON.

---

## 🛠️ Stack Tehnologic

* **Backend:** Java 21, Spring Boot (Data JPA, Security, Web).
* **Frontend:** Thymeleaf, JavaScript, CSS3 (Custom Bordeaux Theme).
* **Bază de date:** MySQL.
* **Instrumente:** Maven, Hibernate, Git.

---

## ⚙️ Instrucțiuni de Instalare și Rulare
*Aceste instrucțiuni sunt destinate configurării proiectului pe un mediu nou.*

1. **Bază de Date:**
   * Creați o bază de date MySQL numită `pasi_medicina`.
   * Verificați setările de conexiune în `src/main/resources/application.properties`.

2. **Compilare și Pornire:**
   * Din IDE (Eclipse/IntelliJ): Rulați clasa `PasiMedicinaApplication.java`.
   * Din Terminal: `mvn spring-boot:run`.

3. **Acces:**
   * Aplicația va fi disponibilă la adresa: `http://localhost:8080`.

---

## 🛡️ Arhitectură
Proiectul urmează arhitectura **MVC (Model-View-Controller)** și utilizează **Spring Data JPA** pentru abstractizarea interacțiunii cu baza de date, asigurând un cod modular și ușor de întreținut.
