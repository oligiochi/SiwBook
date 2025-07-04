package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.models.Recensione;
import it.uniroma3.siwbooks.models.Utente;
import it.uniroma3.siwbooks.service.BookService;
import it.uniroma3.siwbooks.service.RecensioneService;
import it.uniroma3.siwbooks.service.UtenteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static it.uniroma3.siwbooks.authentication.SecurityUtil.isIsAdmin;
import static it.uniroma3.siwbooks.models.Credentials.ADMIN_ROLE;

@Controller
class ReviewController {

    @Autowired
    private RecensioneService reviewService;

    @Autowired
    private UtenteService userService;

    @Autowired
    private BookService bookService;
    @Autowired
    private RecensioneService recensioneService;

    @GetMapping("/book/{book_id}/addReview")
    public String addReview(@PathVariable("book_id") Long bookId,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        Books book = bookService.findById(bookId);
        Utente currentUser = userService.getCurrentUser();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            // Utente sconosciuto / non autenticato
            redirectAttributes.addFlashAttribute("error", "Devi effettuare l'accesso per lasciare una recensione.");
            return "redirect:/login";
        }

        if (reviewService.userHasAlreadyReviewed(book, currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Hai già recensito questo libro.");
            return "redirect:/book/" + bookId + "#addReviewButton";
        }
        model.addAttribute("review", new Recensione());
        model.addAttribute("formAction", "/book/" + book.getId() + "/addReview");
        return "recensioniForm";
    }

    @PostMapping("/book/{book_id}/addReview")
    public String saveReview(@PathVariable("book_id") Long bookId,
                             @Valid @ModelAttribute("review") Recensione review,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model, Principal principal) {

        Books book = bookService.findById(bookId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            model.addAttribute("reviews", reviewService.findByLibro(book));
            List<String> errorMessages = new ArrayList<>();

// 1. Errori di campo, con nome del campo
            bindingResult.getFieldErrors().forEach(fe -> {
                String field = fe.getField();
                String msg   = fe.getDefaultMessage();
                errorMessages.add(String.format("%s: %s", field, msg));
            });

// 2. Errori globali (ObjectError)
            bindingResult.getGlobalErrors().forEach(oe -> {
                errorMessages.add(oe.getDefaultMessage());
            });

            model.addAttribute("errors", errorMessages);

            return "/recensioniForm";
        }
        Utente currentUser = userService.getCurrentUser();
        if (reviewService.userHasAlreadyReviewed(book, currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Hai già recensito questo libro.");
            return "redirect:/accessDenied";
        }
        review.setData(LocalDateTime.now());
        review.setAuthor(userService.getCurrentUser());
        review.setLibro(book);

        reviewService.save(review);

        return "redirect:/book/" + bookId;
    }
    @GetMapping("/book/{book_id}/DeleteReviwe/{recenzione_id}")
    public String deleteReview(@PathVariable("book_id") Long bookId,
                             @PathVariable("recenzione_id") Long recenzioneId,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request,
                             Model model) {
        Recensione review = reviewService.findById(recenzioneId);
        boolean isAdmin = isIsAdmin();
        if (review == null) {
            redirectAttributes.addFlashAttribute("error", "Recensione non trovata.");
        } else if (review.getAuthor().equals(userService.getCurrentUser()) || isAdmin) {
            System.out.println("recensione: cancellata" + review);
            recensioneService.delete(review);
        }else{
            redirectAttributes.addFlashAttribute("error", "Non puoi eliminare questa recensione.");
        }
        return "redirect:/book/" + bookId;
    }

    @GetMapping("/book/{book_id}/UpdateReview/{review_id}")
    public String updateReview(@PathVariable("book_id") Long bookId,
                               @PathVariable("review_id") Long reviewId,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        Recensione review = reviewService.findById(reviewId);
        Books book = bookService.findById(bookId);
        if (userService.getCurrentUser() == review.getAuthor()) {
            model.addAttribute("review", review);
            model.addAttribute("formAction", "/book/" + book.getId() + "/UpdateReview/" + review.getId());
            return "recensioniForm";
        }
        redirectAttributes.addFlashAttribute("error", "Non puoi modificare questa recensione.");
        return "redirect:/book/" + bookId;
    }
    @PostMapping("/book/{book_id}/UpdateReview/{review_id}")
    public String updateReview(@PathVariable("book_id") Long bookId,
                               @PathVariable("review_id") Long reviewId,
                               @Valid @ModelAttribute("review") Recensione review,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("reviews", review);
            List<String> errorMessages = new ArrayList<>();
            bindingResult.getFieldErrors().forEach(fe -> {
                String field = fe.getField();
                String msg   = fe.getDefaultMessage();
                errorMessages.add(String.format("%s: %s", field, msg));
            });

// 2. Errori globali (ObjectError)
            bindingResult.getGlobalErrors().forEach(oe -> {
                errorMessages.add(oe.getDefaultMessage());
            });

            model.addAttribute("errors", errorMessages);

            return "/recensioniForm";

        }
        Utente currentUser = userService.getCurrentUser();
        if (currentUser.equals(reviewService.findById(reviewId).getAuthor())) {
            recensioneService.UpgradeRecensione(reviewService.findById(reviewId),review.getStelle(),review.getCommento(),review.getTitolo());
            return "redirect:/book/" + bookId;
        }else {
            redirectAttributes.addFlashAttribute("error", "Non puoi modificare questa recensione.");
        }
        return "redirect:/book/" + bookId;
    }
}
