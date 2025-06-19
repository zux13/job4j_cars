package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.*;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.PostRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository;
    private FileService fileService;
    private OwnerService ownerService;
    private OwnershipHistoryService ownershipHistoryService;
    private PriceHistoryService priceHistoryService;
    private CarService carService;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        fileService = mock(FileService.class);
        ownerService = mock(OwnerService.class);
        ownershipHistoryService = mock(OwnershipHistoryService.class);
        priceHistoryService = mock(PriceHistoryService.class);
        carService = mock(CarService.class);

        postService = new PostService(postRepository, fileService, ownerService,
                ownershipHistoryService, priceHistoryService, carService);
    }

    @Test
    void whenCreateThenReturnPost() {
        Post post = new Post();
        when(postRepository.create(post)).thenReturn(post);

        Post result = postService.create(post);

        assertThat(result).isEqualTo(post);
        verify(postRepository).create(post);
    }

    @Test
    void whenFindAllThenReturnListOfDtos() {
        Post post = createSamplePost();
        List<Post> posts = List.of(post);
        Map<Integer, String> imagesMap = Map.of(post.getId(), "base64image");

        when(postRepository.findAll()).thenReturn(posts);
        when(fileService.getSingleBase64ImageByPostFrom(posts)).thenReturn(imagesMap);

        List<PostListDto> result = postService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(post.getId());
        assertThat(result.getFirst().getImageBase64()).isEqualTo("base64image");

        verify(postRepository).findAll();
        verify(fileService).getSingleBase64ImageByPostFrom(posts);
    }

    @Test
    void whenGetPostViewDtoByIdThenReturnDto() {
        Post post = createSamplePost();
        post.setId(10);
        List<FileContentDto> fileContents = List.of(
                new FileContentDto(1, "base64-1"),
                new FileContentDto(2, "base64-2")
        );

        when(postRepository.findById(10)).thenReturn(Optional.of(post));
        when(fileService.getAllBase64ImagesFrom(post)).thenReturn(fileContents);

        PostViewDto result = postService.getPostViewDtoById(10);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getBase64images()).containsExactly("base64-1", "base64-2");

        verify(postRepository).findById(10);
        verify(fileService).getAllBase64ImagesFrom(post);
    }

    @Test
    void whenUpdatePostFromThenCallsServicesCorrectly() {
        PostEditDto dto = new PostEditDto();
        dto.setId(5);
        dto.setDescription("new desc");
        dto.setSold(true);
        dto.setBeforePrice(new BigDecimal("100"));
        dto.setPrice(new BigDecimal("200"));
        dto.setNewPhotos(Collections.emptyList());
        dto.setRemovedFileIds(Collections.emptyList());

        User user = new User();
        user.setId(1);

        Car car = new Car();
        car.setId(2);

        Post post = new Post();
        post.setId(5);
        post.setUser(user);
        post.setCar(car);
        post.setSold(false);

        OwnershipHistoryDto currentOwnership = new OwnershipHistoryDto();
        Owner currentOwner = new Owner();

        when(postRepository.findById(5)).thenReturn(Optional.of(post));
        when(ownershipHistoryService.findCurrentOrLastOwnership(dto.getOwnershipHistories())).thenReturn(currentOwnership);
        when(ownerService.getOrCreateCurrentOwnerFrom(currentOwnership, user)).thenReturn(currentOwner);

        postService.updatePostFrom(dto);

        verify(postRepository).findById(5);
        verify(ownershipHistoryService).findCurrentOrLastOwnership(dto.getOwnershipHistories());
        verify(ownerService).getOrCreateCurrentOwnerFrom(currentOwnership, user);
        verify(carService).updateCarAndEngineFrom(car, dto, currentOwner);
        verify(ownershipHistoryService).updateOwnershipHistoryFrom(dto, currentOwnership, currentOwner, user, car);
        verify(priceHistoryService).createAndSavePriceHistory(post, dto.getBeforePrice(), dto.getPrice());
        verify(fileService).saveMultipartFilesList(dto.getNewPhotos(), post);
        verify(fileService).deleteByIds(dto.getRemovedFileIds());
    }

    @Test
    void whenCreatePostFromThenReturnsPost() {
        PostCreateDto dto = new PostCreateDto();
        dto.setDescription("desc");
        dto.setPrice(new BigDecimal("500"));
        dto.setPhotos(Collections.emptyList());
        dto.setOwnershipHistories(Collections.emptyList());

        User user = new User();
        user.setId(10);

        OwnershipHistoryDto currentOwnership = new OwnershipHistoryDto();
        Owner currentOwner = new Owner();
        Car car = new Car();

        when(ownershipHistoryService.findCurrentOrLastOwnership(dto.getOwnershipHistories())).thenReturn(currentOwnership);
        when(ownerService.getOrCreateCurrentOwnerFrom(currentOwnership, user)).thenReturn(currentOwner);
        when(carService.createAndSaveCarAndEngine(dto, currentOwner)).thenReturn(car);
        when(postRepository.create(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        Post post = postService.createPostFrom(dto, user);

        verify(ownershipHistoryService).createAndSaveOwnershipHistoryPeriodsAndOwners(currentOwnership, dto.getOwnershipHistories(), user, car, currentOwner);
        verify(priceHistoryService).createAndSavePriceHistory(post, null, dto.getPrice());
        verify(fileService).saveMultipartFilesList(dto.getPhotos(), post);

        assertThat(post.getDescription()).isEqualTo("desc");
        assertThat(post.getUser()).isEqualTo(user);
        assertThat(post.getCar()).isEqualTo(car);
        assertThat(post.isSold()).isFalse();
    }

    private Post createSamplePost() {
        User user = new User();
        user.setId(1);
        user.setLogin("login");

        Engine engine = new Engine();
        engine.setName("V8");

        Category category = new Category(0, "CategoryName");

        Car car = new Car();
        car.setName("CarName");
        car.setEngine(engine);
        car.setCategory(category);
        car.setCarType(new CarType(0, "TypeName", category));
        car.setBrand(new Brand(0, "BrandName"));
        car.setOwners(Set.of(new Owner()));

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setAfterPrice(new BigDecimal("1000"));
        priceHistory.setBeforePrice(new BigDecimal("900"));

        Post post = new Post();
        post.setId(1);
        post.setCar(car);
        post.setUser(user);
        post.setPriceHistoryList(new java.util.LinkedList<>(List.of(priceHistory)));
        post.setDescription("desc");
        post.setCreated(LocalDateTime.now());
        post.setSold(false);
        return post;
    }
}
