package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.*;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final FileService fileService;
    private final OwnerService ownerService;
    private final OwnershipHistoryService ownershipHistoryService;
    private final PriceHistoryService priceHistoryService;
    private final CarService carService;

    public Post create(Post post) {
        return postRepository.create(post);
    }

    public void update(Post post) {
        postRepository.update(post);
    }

    public void delete(int id) {
        postRepository.delete(id);
    }

    public List<PostListDto> findAll() {
        List<Post> posts = postRepository.findAll();
        Map<Integer, String> imageByPostId = fileService.getSingleBase64ImageByPostFrom(posts);

        return posts.stream()
                .map(post -> toPostListDto(post, imageByPostId.get(post.getId())))
                .toList();
    }

    public List<PostListDto> getFilteredPosts(PostFilterDto postFilterDto) {
        List<Post> posts = postRepository.findByFilter(postFilterDto);
        Map<Integer, String> imageByPostId = fileService.getSingleBase64ImageByPostFrom(posts);

        return posts.stream()
                .map(post -> toPostListDto(post, imageByPostId.get(post.getId())))
                .toList();
    }

    public long getFilteredPostsCount(PostFilterDto filer) {
        return postRepository.countByFilter(filer);
    }

    public PostViewDto getPostViewDtoById(int id) {
        Post post = findById(id);
        List<String> images = fileService.getAllBase64ImagesFrom(post)
                .stream()
                .map(FileContentDto :: getBase64)
                .toList();

        return toPostViewDto(post, images);
    }

    public Post findById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пост с id " + id + " не найден"));
    }

    public void updatePostFrom(PostEditDto dto) {
        Post post = findById(dto.getId());
        User user = post.getUser();
        Car car = post.getCar();

        OwnershipHistoryDto currentOwnership = ownershipHistoryService.findCurrentOrLastOwnership(dto.getOwnershipHistories());
        Owner currentOwner = ownerService.getOrCreateCurrentOwnerFrom(currentOwnership, user);
        carService.updateCarAndEngineFrom(car, dto, currentOwner);

        ownershipHistoryService.updateOwnershipHistoryFrom(dto, currentOwnership, currentOwner, user, car);

        updatePostDescriptionAndStatus(post, dto.getDescription(), dto.isSold());

        if (!dto.getBeforePrice().equals(dto.getPrice())) {
            priceHistoryService.createAndSavePriceHistory(post, dto.getBeforePrice(), dto.getPrice());
        }

        fileService.saveMultipartFilesList(dto.getNewPhotos(), post);
        fileService.deleteByIds(dto.getRemovedFileIds());
    }

    public Post createPostFrom(PostCreateDto dto, User user) {
        OwnershipHistoryDto currentOwnership = ownershipHistoryService.findCurrentOrLastOwnership(dto.getOwnershipHistories());
        Owner currentOwner = ownerService.getOrCreateCurrentOwnerFrom(currentOwnership, user);
        Car car = carService.createAndSaveCarAndEngine(dto, currentOwner);

        ownershipHistoryService.createAndSaveOwnershipHistoryPeriodsAndOwners(
                                                        currentOwnership,
                                                        dto.getOwnershipHistories(),
                                                        user,
                                                        car,
                                                        currentOwner);

        Post post = createAndSavePost(car, user, dto.getDescription());
        priceHistoryService.createAndSavePriceHistory(post, null, dto.getPrice());

        fileService.saveMultipartFilesList(dto.getPhotos(), post);

        return post;
    }

    private void updatePostDescriptionAndStatus(Post post, String description, boolean status) {
        post.setDescription(description);
        post.setSold(status);
        update(post);
    }

    private Post createAndSavePost(Car car, User user, String description) {
        Post post = new Post();
        post.setDescription(description);
        post.setCreated(LocalDateTime.now());
        post.setUser(user);
        post.setCar(car);
        post.setSold(false);

        return create(post);
    }

    private PostListDto toPostListDto(Post post, String imageBase64) {
        PostListDto dto = new PostListDto();
        dto.setId(post.getId());
        dto.setUserId(post.getUser().getId());
        dto.setCarName(post.getCar().getName());
        dto.setPrice(post.getPriceHistoryList().getFirst().getAfterPrice());
        dto.setEngineName(post.getCar().getEngine().getName());
        dto.setImageBase64(imageBase64);
        dto.setSold(post.isSold());

        return dto;
    }

    private PostViewDto toPostViewDto(Post post, List<String> images) {
        PostViewDto dto = new PostViewDto();
        dto.setId(post.getId());
        dto.setUserId(post.getUser().getId());
        dto.setDescription(post.getDescription());
        dto.setCreated(post.getCreated());
        dto.setPreviousPrice(post.getPriceHistoryList().getFirst().getBeforePrice());
        dto.setCurrentPrice(post.getPriceHistoryList().getFirst().getAfterPrice());
        dto.setCarName(post.getCar().getName());
        dto.setEngineName(post.getCar().getEngine().getName());
        dto.setCategoryName(post.getCar().getCategory().getName());
        dto.setTypeName(post.getCar().getCarType().getName());
        dto.setBrandName(post.getCar().getBrand().getName());
        dto.setAuthorName(post.getUser().getLogin());
        dto.setOwnersCount(post.getCar().getOwners().size());
        dto.setSold(post.isSold());
        dto.setBase64images(images);
        return dto;
    }

}
