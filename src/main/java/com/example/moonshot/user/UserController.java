package com.example.moonshot.user;

import com.example.moonshot.user.dto.UserRequest;
import com.example.moonshot.user.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return UserResponse.from(user);
    }

    @GetMapping("/email/{email}")
    public UserResponse getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return UserResponse.from(user);
    }

    @GetMapping("/phone/{phone}")
    public UserResponse getUserByPhone(@PathVariable String phone) {
        User user = userService.getUserByPhone(phone);
        return UserResponse.from(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody UserRequest userDto) {
        User user = userService.createUser(userDto);
        return UserResponse.from(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
