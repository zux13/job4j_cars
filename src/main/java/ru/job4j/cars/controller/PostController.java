package ru.job4j.cars.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cars.dto.*;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.*;
import ru.job4j.cars.util.JsonUtils;

import java.util.List;

@Controller
@AllArgsConstructor
public class PostController {
    private final FileService fileService;
    private final PostService postService;
    private final BrandService brandService;
    private final CarTypeService carTypeService;
    private final CategoryService categoryService;
    private final OwnershipHistoryService ownershipHistoryService;

    @GetMapping({"/", "/index"})
    public String getFilteredPosts(@ModelAttribute PostFilterDto filter,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "6") int size,
                                   Model model) {

        filter.setLimit(size);
        filter.setOffset((page - 1) * size);

        List<PostListDto> posts = postService.getFilteredPosts(filter);

        long total = postService.getFilteredPostsCount(filter);
        int totalPages = (int) Math.ceil((double) total / size);

        model.addAttribute("posts", posts);
        model.addAttribute("filter", filter);
        model.addAttribute("filterEmpty", filter.isEmpty());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("brands", brandService.findAll());
        model.addAttribute("pageTitle", "Каталог автомобилей");
        return "index";
    }

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable int id, Model model) {
        PostViewDto post = postService.getPostViewDtoById(id);
        model.addAttribute("post", post);
        model.addAttribute("pageTitle", post.getCarName());
        return "posts/view";
    }

    @GetMapping("/posts/create")
    public String newPost(Model model) {
        String typesJson = JsonUtils.toJson(carTypeService.findAll());
        model.addAttribute("typesJson", typesJson);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("brands", brandService.findAll());
        model.addAttribute("pageTitle", "Создание объявления");
        return "posts/create";
    }

    @PostMapping("/posts/create")
    public String createPost(@ModelAttribute PostCreateDto postCreateDto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Post post = postService.createPostFrom(postCreateDto, user);
        return "redirect:/posts/" + post.getId();

    }

    @GetMapping("/posts/edit/{id}")
    public String editPost(@PathVariable int id, Model model) {
        Post post = postService.findById(id);
        String typesJson = JsonUtils.toJson(carTypeService.findAll());
        model.addAttribute("post", post);
        model.addAttribute("imageList", fileService.getAllBase64ImagesFrom(post));
        model.addAttribute("ownershipHistories", ownershipHistoryService.findByCarId(post.getCar().getId()));
        model.addAttribute("typesJson", typesJson);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("brands", brandService.findAll());
        model.addAttribute("pageTitle", "Редактирование объявления");
        return "posts/edit";
    }

    @PostMapping("/posts/edit/{id}")
    public String saveEditedPost(@PathVariable int id, @ModelAttribute PostEditDto dto) {
        postService.updatePostFrom(dto);
        return "redirect:/posts/" + id;
    }

}
