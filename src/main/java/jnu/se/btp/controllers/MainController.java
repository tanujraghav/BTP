package jnu.se.btp.controllers;

import jnu.se.btp.entities.ResourceFileEntity;
import jnu.se.btp.services.LibraryService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    String emailGlobal = null;

    List<String> Keys = Arrays.asList(
            "mykey"
    );

    List<String> Semesters = Arrays.asList("I - CSE", "I - ECE", "I - Common", "II - CSE", "II - ECE", "II - Common", "III - CSE", "III - ECE", "III - Common", "IV - CSE", "IV - ECE", "IV - Common", "V - CSE", "V - ECE", "V - Common", "VI - CSE", "VI - ECE", "VI - Common", "VII - CSE", "VII - ECE", "VII - Common", "VIII - CSE", "VIII - ECE", "VIII - Common", "IX - CSE", "IX - ECE", "IX - Common", "X - CSE", "X - ECE", "X- Common", "Miscellaneous");

    List<String> Format = Arrays.asList("application/pdf", "application/zip", "image/png", "video/mp4");

    private LibraryService libraryService;

    @Autowired
    public MainController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("")
    public String index(
            Model model,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String orderBy,
            @RequestParam(defaultValue = "_") String keyword
    ) {

        Page<ResourceFileEntity> page = libraryService.showAll(pageNo, sortBy, orderBy, keyword);

        model.addAttribute("resourceList", page.getContent());
        model.addAttribute("allPages", page.getTotalPages());
        model.addAttribute("allResources", page.getTotalElements());

        model.addAttribute("thisPage", pageNo);

        model.addAttribute("sortBy", sortBy);
        model.addAttribute("orderBy", orderBy);

        model.addAttribute("toggleSort", orderBy.equals("asc") ? "desc" : "asc");

        model.addAttribute("keyword", keyword);

        model.addAttribute("resource", new ResourceFileEntity());

        return "index";
    }

    @PostMapping("/getOTP")
    public String getOTP(
            @ModelAttribute("email") String email,
            RedirectAttributes redirectAttributes
    ) {

        if (email != null) {
            email = email + "_soe@jnu.ac.in";
            emailGlobal = email;
        }

        libraryService.otp(email);

        redirectAttributes.addFlashAttribute("message", "OTP sent to " + email);
        redirectAttributes.addFlashAttribute("type", "success");

        return "redirect:/";
    }

    @SneakyThrows
    @PostMapping("/save")
    public String save(
            @ModelAttribute("resource") ResourceFileEntity resource,
            @ModelAttribute("otp") int otp,
            @RequestParam("file") MultipartFile multipartFile,
            RedirectAttributes redirectAttributes
    ) {

        if (!Format.contains(multipartFile.getContentType())) {
            redirectAttributes.addFlashAttribute("message", "Unable to upload file! Reason: Unsupported Format");
            redirectAttributes.addFlashAttribute("type", "failure");

            return "redirect:/";
        }

        String extension = multipartFile.getOriginalFilename().split("\\.")[1];
        resource.setExtension(extension);

        resource.setHash(DigestUtils.sha512Hex(multipartFile.getBytes()));

        if (!Semesters.contains(resource.getSemester()))
            resource.setSemester("Miscellaneous");

        String response = libraryService.save(resource, otp, emailGlobal, multipartFile);

        if (response == "OTP") {
            redirectAttributes.addFlashAttribute("message", "Unable to upload file! Reason: Invalid OTP");
            redirectAttributes.addFlashAttribute("type", "failure");

            return "redirect:/";
        }

        if (response == "hash") {
            redirectAttributes.addFlashAttribute("message", "Unable to upload file! Reason: <b>File already exists</b>");
            redirectAttributes.addFlashAttribute("type", "failure");

            return "redirect:/";
        }

        emailGlobal = null;

        redirectAttributes.addFlashAttribute("message", "File uploaded successfully!");
        redirectAttributes.addFlashAttribute("type", "success");

        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(
            @ModelAttribute("id") Long id,
            @ModelAttribute("key") String key,
            RedirectAttributes redirectAttributes
    ) {

        String by = null;

        if (key.equals(Keys.get(0)))
            by = "Tanuj Raghav";

        if (by == null) {
            redirectAttributes.addFlashAttribute("message", "Unable to delete file! Reason: Invalid Key");
            redirectAttributes.addFlashAttribute("type", "failure");

            return "redirect:/";
        }

        String response = libraryService.delete(id, by);

        if (response == null) {
            redirectAttributes.addFlashAttribute("message", "Unable to delete file! Reason: File not found");
            redirectAttributes.addFlashAttribute("type", "failure");

            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("message", "Successfully deleted resource #" + id);
        redirectAttributes.addFlashAttribute("type", "success");

        return "redirect:/";
    }

}
