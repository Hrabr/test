package ua.goit.group2notes.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.goit.group2notes.errorHandling.UserAlreadyExistsException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/list")
    public String listUsers(Model model) {
        List<UserDto> all = userService.getAll();
        model.addAttribute("users", all);
        return "userList";

    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable UUID id, Map<String, Object> model) {
        UserDto user = userService.getUserById(id);
        user.setPassword("Enter your password");
        model.put("user", user);
        return "registrationR";
    }

    @PostMapping("/edit/{id}")
    public String userPostEdit(Authentication authentication, @PathVariable("id") UUID id, @ModelAttribute("user") @Valid UserDto userDto, BindingResult bindingResult, Map<String, Object> model) {
        UserDto authUser = userService.findUserByUsername(authentication.getName());
        UserDto user = userService.getUserById(id);
        if (bindingResult.hasErrors()) {
            model.put("user", userDto);
            return "registrationR";
        }
        user.setUsername(userDto.getUsername());
        if (!userDto.getPassword().equals("Enter your password")) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        user.setUserRole(userDto.getUserRole());
        userService.editUser(user);

        if (authUser.getId() == user.getId() && !authUser.getUsername().equals(user.getUsername())) {
            authentication.setAuthenticated(false);
        }
        return "redirect:/users/list";

    }

    @GetMapping("delete/{id}")
    public String deleteUser(@PathVariable UUID id, Authentication authentication) {
        UserDto authUser = userService.findUserByUsername(authentication.getName());
        userService.deleteUser(id);
        if (authUser.getId().equals(id)) {
            authentication.setAuthenticated(false);
        }
        return "redirect:/users/list";
    }

    @GetMapping(path = "/registration")
    public String getRegistrationForm() {
        return "registration";
    }

    @PostMapping(path = "/registration")
    public String registerUser(@ModelAttribute("userDto") @Valid UserDto userDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        try {
            userDto.setUserRole(UserRole.ROLE_USER);
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userService.save(userDto);
        } catch (UserAlreadyExistsException ex) {
            model.addAttribute("message", ex.getMessage());
            return "registration";
        }
        return "login";
    }

    @ModelAttribute
    public UserDto getDefaultUserDto() {
        return new UserDto();
    }
}
