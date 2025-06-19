package ru.job4j.cars.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ru.job4j.cars.dto.*;
import ru.job4j.cars.model.*;
import ru.job4j.cars.service.*;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PostControllerTest {

    private FileService fileService;
    private PostService postService;
    private BrandService brandService;
    private CarTypeService carTypeService;
    private CategoryService categoryService;
    private OwnershipHistoryService ownershipHistoryService;

    private PostController postController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        fileService = mock(FileService.class);
        postService = mock(PostService.class);
        brandService = mock(BrandService.class);
        carTypeService = mock(CarTypeService.class);
        categoryService = mock(CategoryService.class);
        ownershipHistoryService = mock(OwnershipHistoryService.class);

        postController = new PostController(fileService, postService, brandService, carTypeService, categoryService, ownershipHistoryService);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void whenGetFilteredPostsThenReturnsIndexViewWithModel() throws Exception {
        List<PostListDto> posts = List.of(new PostListDto());
        List<Brand> brands = List.of(new Brand());

        when(postService.getFilteredPosts(any(PostFilterDto.class))).thenReturn(posts);
        when(postService.getFilteredPostsCount(any(PostFilterDto.class))).thenReturn(10L);
        when(brandService.findAll()).thenReturn(brands);

        mockMvc.perform(get("/index")
                        .param("page", "1")
                        .param("size", "6"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("filter"))
                .andExpect(model().attributeExists("filterEmpty"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("size"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attribute("pageTitle", "Каталог автомобилей"));

        verify(postService).getFilteredPosts(any(PostFilterDto.class));
        verify(postService).getFilteredPostsCount(any(PostFilterDto.class));
        verify(brandService).findAll();
    }

    @Test
    void whenGetPostThenReturnsPostView() throws Exception {
        PostViewDto postViewDto = new PostViewDto();
        postViewDto.setCarName("CarName");
        when(postService.getPostViewDtoById(1)).thenReturn(postViewDto);

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/view"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("pageTitle", "CarName"));

        verify(postService).getPostViewDtoById(1);
    }

    @Test
    void whenNewPostThenReturnsCreateViewWithModel() throws Exception {
        CarTypeDto carTypeDto = new CarTypeDto();
        carTypeDto.setId(1);
        carTypeDto.setName("Sedan");
        carTypeDto.setCategoryId(1);
        List<CarTypeDto> carTypes = List.of(carTypeDto);
        List<Category> categories = List.of(new Category());
        List<Brand> brands = List.of(new Brand());

        when(carTypeService.findAll()).thenReturn(carTypes);
        when(categoryService.findAll()).thenReturn(categories);
        when(brandService.findAll()).thenReturn(brands);

        mockMvc.perform(get("/posts/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/create"))
                .andExpect(model().attributeExists("typesJson"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attribute("pageTitle", "Создание объявления"));

        verify(carTypeService).findAll();
        verify(categoryService).findAll();
        verify(brandService).findAll();
    }

    @Test
    void whenCreatePostThenRedirects() throws Exception {
        PostCreateDto dto = new PostCreateDto();
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setId(42);

        when(postService.createPostFrom(any(PostCreateDto.class), any(User.class))).thenReturn(post);

        mockMvc.perform(post("/posts/create")
                        .sessionAttr("user", user)
                        .flashAttr("postCreateDto", dto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/42"));

        verify(postService).createPostFrom(any(PostCreateDto.class), eq(user));
    }

    @Test
    void whenEditPostThenReturnsEditViewWithModel() throws Exception {
        Car car = new Car();
        car.setId(1);
        Post post = new Post();
        post.setId(5);
        post.setCar(car);
        CarTypeDto carTypeDto = new CarTypeDto();
        carTypeDto.setId(1);
        carTypeDto.setName("Sedan");
        carTypeDto.setCategoryId(1);
        List<CarTypeDto> carTypes = List.of(carTypeDto);
        List<Category> categories = List.of(new Category());
        List<Brand> brands = List.of(new Brand());

        when(postService.findById(5)).thenReturn(post);
        when(fileService.getAllBase64ImagesFrom(post)).thenReturn(List.of());
        when(ownershipHistoryService.findByCarId(car.getId())).thenReturn(List.of());
        when(carTypeService.findAll()).thenReturn(carTypes);
        when(categoryService.findAll()).thenReturn(categories);
        when(brandService.findAll()).thenReturn(brands);

        mockMvc.perform(get("/posts/edit/5"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/edit"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("imageList"))
                .andExpect(model().attributeExists("ownershipHistories"))
                .andExpect(model().attributeExists("typesJson"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attribute("pageTitle", "Редактирование объявления"));

        verify(postService).findById(5);
        verify(fileService).getAllBase64ImagesFrom(post);
        verify(ownershipHistoryService).findByCarId(car.getId());
        verify(carTypeService).findAll();
        verify(categoryService).findAll();
        verify(brandService).findAll();
    }

    @Test
    void whenSaveEditedPostThenRedirects() throws Exception {
        PostEditDto dto = new PostEditDto();
        dto.setId(3);

        mockMvc.perform(post("/posts/edit/3")
                        .flashAttr("dto", dto)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/3"));

        verify(postService).updatePostFrom(argThat(argument -> argument.getId() == 3));
    }

}
