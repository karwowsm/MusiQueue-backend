package pl.com.karwowsm.musiqueue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    public static final Path BASE_DIR_PATH = buildPath(System.getProperty("user.home"), ".musiqueue");
    public static final Path TRACKS_DIR_PATH = buildPath(BASE_DIR_PATH.toString());
    public static final Path IMAGES_DIR_PATH = buildPath(BASE_DIR_PATH.toString(), "images");

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/images/**")
            .addResourceLocations("file:" + IMAGES_DIR_PATH + File.separator);
    }

    private static Path buildPath(String... dirs) {
        return Paths.get(String.join(File.separator, dirs));
    }
}
