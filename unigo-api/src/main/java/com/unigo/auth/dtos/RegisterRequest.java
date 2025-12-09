package com.unigo.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String nombre;

    @NotBlank(message = "Username es obligatorio")
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    private String username;

    @NotBlank(message = "Email es obligatorio")
    @Email(message = "Email inválido")
//  todo:  @Pattern(regex = "", message = "Solo emails corporativos")
    private String email;

    @NotBlank(message = "Contraseña obligatoria")
    @Size(min = 8, message = "Mínimo 8 caracteres")
    private String password;

}
