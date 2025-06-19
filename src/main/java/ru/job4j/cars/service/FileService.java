package ru.job4j.cars.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileContentDto;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final String storageDirectory;

    public FileService(
        FileRepository fileRepository,
        @Value("${file.directory}") String storageDirectory
    ) {
        this.fileRepository = fileRepository;
        this.storageDirectory = storageDirectory;
    }

    @PostConstruct
    private void createStorageDirectory() {
        try {
            Files.createDirectories(Path.of(storageDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<File> save(FileDto fileDto) {
        return fileRepository.save(toEntity(fileDto));
    }

    public Optional<FileDto> getFileById(int id) {
        Optional<File> optionalFile = fileRepository.findById(id);
        return optionalFile.map(this::toFileDto);
    }

    public void deleteById(int id) {
        var fileOptional = fileRepository.findById(id);
        if (fileOptional.isPresent()) {
            deleteFile(fileOptional.get().getPath());
            fileRepository.delete(id);
        }
    }

    public void deleteByIds(List<Integer> ids) {
        List<File> files = fileRepository.findByIds(ids);
        for (File file : files) {
            deleteFile(file.getPath());
        }
        fileRepository.deleteByIds(ids);
    }

    public void saveMultipartFilesList(List<MultipartFile> files, Post post) {
        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                continue;
            }
            try {
                save(toFileDto(file, post));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Map<Integer, String> getSingleBase64ImageByPostFrom(List<Post> posts) {
        Map<Integer, String> result = new HashMap<>();
        for (Post post : posts) {
            var files = post.getFiles();
            if (!files.isEmpty()) {
                var file = files.getFirst();
                byte[] bytes = readFileAsBytes(file.getPath());
                result.put(post.getId(), Base64.getEncoder().encodeToString(bytes));
            }
        }
        return result;
    }

    public List<FileContentDto> getAllBase64ImagesFrom(Post post) {
        List<FileContentDto> images = new ArrayList<>();
        for (File file : post.getFiles()) {
            FileContentDto dto = new FileContentDto();
            byte[] bytes = readFileAsBytes(file.getPath());
            dto.setId(file.getId());
            dto.setBase64(Base64.getEncoder().encodeToString(bytes));
            images.add(dto);
        }
        return images;
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNewFilePath(String sourceName) {
        return storageDirectory + java.io.File.separator + UUID.randomUUID() + sourceName;
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File toEntity(FileDto fileDto) {
        var path = getNewFilePath(fileDto.getName());
        writeFileBytes(path, fileDto.getContent());

        File file = new File();
        file.setName(fileDto.getName());
        file.setPost(fileDto.getPost());
        file.setPath(path);
        return file;
    }

    private FileDto toFileDto(File file) {
        FileDto fileDto = new FileDto();
        fileDto.setName(file.getName());
        fileDto.setPost(file.getPost());
        fileDto.setContent(readFileAsBytes(file.getPath()));
        return fileDto;
    }

    private FileDto toFileDto(MultipartFile file, Post post) throws IOException {
        FileDto fileDto = new FileDto();
        fileDto.setName(file.getOriginalFilename());
        fileDto.setPost(post);
        fileDto.setContent(file.getBytes());
        return fileDto;
    }

}
