package com.decagon.chompapp.dtos;

import com.decagon.chompapp.enums.Gender;
import com.decagon.chompapp.models.Cart;
import com.decagon.chompapp.models.Role;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
  private String firstName;
  private String lastName;
  private String username;
  private String confirmationToken;
  private Set<Role> roles;
  private Gender gender;
  private Long userId;
  private CartDto cartDto;
  private String email;
}
