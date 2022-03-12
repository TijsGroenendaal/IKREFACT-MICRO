package nl.hetckm.bouncer.user;

import nl.hetckm.base.model.AppUser;
import nl.hetckm.base.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERUSER')")
@RestController()
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserPrincipalService userPrincipalService;

    @Autowired
    public UserController(
            UserService userService,
            @Lazy UserPrincipalService userPrincipalService
    ){
        this.userService = userService;
        this.userPrincipalService = userPrincipalService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    UserResponse createUser(@RequestBody AppUser appUser) {
        return new UserResponse(userService.create(appUser));
    }

    @GetMapping()
    public @ResponseBody
    Page<UserResponse> getUsersByPlatform(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<AppUser> users = userService.findAll(pageable);
        List<UserResponse> userResponseList = new ArrayList<>();
        users.forEach(user -> userResponseList.add(new UserResponse(user)));
        return new PageImpl<>(userResponseList, pageable, users.getTotalElements());
    }

    @GetMapping("/{id}")
    public @ResponseBody
    UserResponse getUser(@PathVariable UUID id) {
        return new UserResponse(userService.findOne(id));
    }

    @PatchMapping("/{id}")
    public @ResponseBody
    UserResponse updateUser(
            @PathVariable UUID id,
            @RequestBody AppUser appUser
    ) {
        return new UserResponse(userService.update(id, appUser));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.delete(id);
    }

    @GetMapping("userdetails/{name}")
    public UserDetails getUserDetails(@PathVariable String name) {
        return userPrincipalService.loadUserByUsername(name);
    }
}
