package ua.goit.group2notes.note;

import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.goit.group2notes.security.SecurityConfiguration;
import ua.goit.group2notes.user.UserService;
import ua.goit.group2notes.user.UsersDetailsService;

import javax.servlet.Filter;
import javax.servlet.ServletContext;

import java.beans.BeanProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.JRE.JAVA_11;
import static org.junit.jupiter.api.condition.JRE.JAVA_9;
import static org.junit.jupiter.api.condition.OS.WINDOWS;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebAppConfiguration
@SpringBootTest

@ActiveProfiles({"test"})
@TestPropertySource("classpath:application.properties")
class NoteControllerTest {
    @MockBean
    private NoteService noteService;
//    @Autowired
//    private Filter springSecurityFilterChain;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)

                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser("a")
    public void noteCreateShouldNoteForm() throws Exception {
     List<NoteDto> list=new ArrayList<>();

        NoteDto noteDto=new NoteDto();
        noteDto.setTitle("fsdgdfg");
        noteDto.setText("fddfdhgf");
        noteDto.setId(UUID.fromString("585f85a2-59a1-4e47-b37b-4d8003db6796"));
        noteDto.setAccessType(NoteAccessType.valueOf("PRIVATE"));
     list.add(noteDto);
        final OngoingStubbing<List<NoteDto>> listOngoingStubbing = when(noteService.getAll()).thenReturn(List.of(noteDto));
        mockMvc.perform(get("/note/list"))

                .andExpect(MockMvcResultMatchers.model().attribute("notes",listOngoingStubbing))
//                .andExpect(model().attribute("shate","/note/list"))
                .andDo(print())
//                .andExpect(MockMvcResultMatchers.model().attribute("notes", hasSize(1)))


                .andExpect(content().string("notes"))
        ;
    }

    @Test
    public void givenWac_whenServletContext_thenItProvidesGreetController() {
        ServletContext servletContext = context.getServletContext();

       assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(context.getBean("noteController"));
    }
    @WithMockUser("a")
    @Test
    public void givenHomePageURI_whenMockMVC_thenReturnsIndexJSPViewName() throws Exception {
        this.mockMvc
        .perform(get("/note/list")).andDo(print())
                .andExpect(content().string("notes"));}

    @Test
    public void noteCreateWithIncorrectUserShouldReturnStatus302AndRedirectToLogin() throws Exception {
        mockMvc.perform(get("/note/create"))
                .andExpect(MockMvcResultMatchers.status().is(302))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login"));

    }

    @WithMockUser("ADMIN")
    @Test
    public void correctLoginTest() throws Exception {
        mockMvc.perform(formLogin())
                .andDo(print())
                .andExpect(authenticated());
    }

    @Test
    public void badCredentials() throws Exception {
        this.mockMvc.perform(post("/login").param("username", "a"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}