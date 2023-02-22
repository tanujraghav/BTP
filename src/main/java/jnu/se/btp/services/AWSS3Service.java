package jnu.se.btp.services;

import com.amazonaws.services.s3.AmazonS3;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

@Service
@Component
public class AWSS3Service {

    private final String bucketName = "btech-project";

    private final AmazonS3 amazonS3;

    public AWSS3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @SneakyThrows
    public void upload(String key, @NotNull MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        new FileOutputStream(file).write(multipartFile.getBytes());
        amazonS3.putObject(bucketName, key, file);
        file.delete();
    }

    public void delete(String key) {
        amazonS3.deleteObject(bucketName, key);
    }

}
