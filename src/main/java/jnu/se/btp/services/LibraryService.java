package jnu.se.btp.services;

import jnu.se.btp.entities.ResourceFileEntity;
import jnu.se.btp.repositories.DAOInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Component
public class LibraryService {

    private final DAOInterface daoInterface;

    private final EMailService eMailService;

    private final OTPService otpService;

    private final AWSS3Service awss3Service;

    public LibraryService(DAOInterface daoInterface, EMailService eMailService, OTPService otpService, AWSS3Service awss3Service) {
        this.daoInterface = daoInterface;
        this.eMailService = eMailService;
        this.otpService = otpService;
        this.awss3Service = awss3Service;
    }

    public Page<ResourceFileEntity> showAll(int pageNo, String sortBy, @NotNull String orderBy, @NotNull String keyword) {
        Sort sort = orderBy.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return daoInterface.findAll(keyword.toLowerCase(), PageRequest.of(pageNo - 1, 3, sort));
    }

    public void otp(String key) {
        if (key != null) {
            eMailService.OTPMail(otpService.generateOTP(key), key);
        }
    }

    public String save(ResourceFileEntity resource, int otp, String email, MultipartFile multipartFile) {

        if (email == null || otp != otpService.readOTP(email)) {
            return "OTP";
        }

        if (daoInterface.findResourceFileEntitiesByHash(resource.getHash()) != null) {
            return "hash";
        }

        String key = null;

        if (resource.getAuthor() != null)
            key = resource.getAuthor() + " - ";

        key = key + resource.getTitle() + " (" + resource.getYear() + ", btp.jnuSE [" + resource.getHash().charAt(0) + resource.getHash().charAt(1) + resource.getHash().charAt(2) + resource.getHash().charAt(3) + resource.getHash().charAt(4) + resource.getHash().charAt(5) + "])." + resource.getExtension();

        awss3Service.upload(key, multipartFile);

        resource.setUrl("https://btech-project.s3.ap-south-1.amazonaws.com" + key);

        otpService.invalidateOTP(email);
        daoInterface.save(resource);

        return null;
    }

    public String delete(Long id, String by) {

        Optional<ResourceFileEntity> resourceFileEntity = daoInterface.findById(id);

        if (by != null && resourceFileEntity.isPresent()) {

            String key = null;

            if (resourceFileEntity.get().getAuthor() != null)
                key = resourceFileEntity.get().getAuthor() + " - ";

            key = key + resourceFileEntity.get().getTitle() + " (" + resourceFileEntity.get().getYear() + ", btp.jnuSE [" + resourceFileEntity.get().getHash().charAt(0) + resourceFileEntity.get().getHash().charAt(1) + resourceFileEntity.get().getHash().charAt(2) + resourceFileEntity.get().getHash().charAt(3) + resourceFileEntity.get().getHash().charAt(4) + resourceFileEntity.get().getHash().charAt(5) + "])." + resourceFileEntity.get().getExtension();

            awss3Service.delete(key);

            eMailService.deleteNotification(resourceFileEntity.get());
            daoInterface.deleteById(id);

            return "OK";
        }

        return null;
    }

}
