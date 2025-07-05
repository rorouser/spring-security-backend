package com.security.artifact.persistence.user;


import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserGraphQLController {

    private final UserService userService;

    @QueryMapping
    public UserResponse user(@Argument Integer id) {
        UserResponse user =  userService.user(id);
        return user;
    }

    /*@MutationMapping
    public User crearUsuario(@Argument String nombre, @Argument String email, Authentication authentication) {
        // Validar permisos aquí si quieres
        return userService.crearUsuario(nombre, email);
    }*/
}