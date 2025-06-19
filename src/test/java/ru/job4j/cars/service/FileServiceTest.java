package ru.job4j.cars.service;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import ru.job4j.cars.dto.FileContentDto;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.FileRepository;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FileServiceTest {

    private FileRepository fileRepository;
    private FileService fileService;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        fileRepository = mock(FileRepository.class);
        tempDir = Files.createTempDirectory("test-files");
        Files.createDirectories(tempDir);
        fileService = new FileService(fileRepository, tempDir.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(java.io.File::delete);
    }

    @Test
    void whenSaveThenRepositoryCalled() {
        Post post = new Post();
        byte[] content = "test".getBytes();
        FileDto dto = new FileDto("photo.jpg", post, content);

        ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
        when(fileRepository.save(any())).thenReturn(Optional.of(new File()));

        fileService.save(dto);

        verify(fileRepository).save(captor.capture());
        assertThat(Files.exists(Path.of(captor.getValue().getPath()))).isTrue();
    }

    @Test
    void whenGetFileByIdThenReturnFileDto() throws IOException {
        File file = new File();
        file.setId(1);
        file.setName("image.jpg");
        file.setPath(Files.writeString(tempDir.resolve("image.jpg"), "hello").toString());
        file.setPost(new Post());

        when(fileRepository.findById(1)).thenReturn(Optional.of(file));

        var result = fileService.getFileById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("image.jpg");
        assertThat(result.get().getContent()).containsExactly("hello".getBytes());
    }

    @Test
    void whenDeleteByIdThenFileIsDeletedFromDiskAndRepository() throws IOException {
        Path filePath = Files.write(tempDir.resolve("to_delete.jpg"), "bye".getBytes());

        File file = new File();
        file.setId(2);
        file.setPath(filePath.toString());

        when(fileRepository.findById(2)).thenReturn(Optional.of(file));

        fileService.deleteById(2);

        assertThat(Files.exists(filePath)).isFalse();
        verify(fileRepository).delete(2);
    }

    @Test
    void whenGetBase64ImagesThenReturnEncodedContent() throws IOException {
        File file = new File();
        file.setId(10);
        file.setPath(Files.write(tempDir.resolve("img.jpg"), "base64".getBytes()).toString());

        Post post = new Post();
        post.setFiles(List.of(file));

        List<FileContentDto> images = fileService.getAllBase64ImagesFrom(post);

        assertThat(images).hasSize(1);
        assertThat(images.getFirst().getId()).isEqualTo(10);
        assertThat(images.getFirst().getBase64())
                .isEqualTo(Base64.getEncoder().encodeToString("base64".getBytes()));
    }

    @Test
    void whenSaveMultipartFilesListThenValidFilesAreSaved() {
        Post post = new Post();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "hello".getBytes()
        );

        when(fileRepository.save(any())).thenReturn(Optional.of(new File()));

        fileService.saveMultipartFilesList(List.of(mockFile), post);

        verify(fileRepository).save(any());
    }

    @Test
    void whenSaveMultipartFilesListWithEmptyFileThenSkip() {
        Post post = new Post();
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]
        );

        fileService.saveMultipartFilesList(List.of(emptyFile), post);

        verify(fileRepository, never()).save(any());
    }
}
