package pl.com.karwowsm.musiqueue;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import pl.com.karwowsm.musiqueue.config.ResourcesConfig;
import pl.com.karwowsm.musiqueue.persistence.model.FileEntity;
import pl.com.karwowsm.musiqueue.persistence.repository.FileEntityRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@EnableRetry
@SpringBootApplication
@RequiredArgsConstructor
public class MusiQueueApplication {

    private final FileEntityRepository fileEntityRepository;

    public static void main(String[] args) {
        SpringApplication.run(MusiQueueApplication.class, args);
    }

    @PostConstruct
    public void initializeResources() {
        try {
            Files.createDirectories(ResourcesConfig.TRACKS_DIR_PATH);
            Files.createDirectories(ResourcesConfig.IMAGES_DIR_PATH);

            List<FileEntity> orphanedFileEntities = fileEntityRepository.findAll().stream()
                .filter(fileEntity -> Files.notExists(Paths.get(fileEntity.getPath())))
                .collect(Collectors.toList());
            for (FileEntity fileEntity : orphanedFileEntities) {
                Path path = ResourcesConfig.BASE_DIR_PATH.resolve(fileEntity.getPath());
                Files.write(path, fileEntity.getContent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
