package com.decagon.chompapp.services.Impl;


import com.decagon.chompapp.dtos.*;
import com.decagon.chompapp.models.Cart;
import com.decagon.chompapp.models.CartItem;
import com.decagon.chompapp.models.Product;
import com.decagon.chompapp.models.User;
import com.decagon.chompapp.repositories.BlackListedTokenRepository;
import com.decagon.chompapp.repositories.UserRepository;
import com.decagon.chompapp.security.JwtTokenProvider;
import com.decagon.chompapp.services.BlackListService;
import com.decagon.chompapp.services.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletResponse httpServletResponse;
    private final BlackListService blackListService;
    private final BlackListedTokenRepository blackListedTokenRepository;
    private final HttpServletRequest httpServletRequest;
    private final UserRepository userRepository;



//    @Override
//    public ResponseEntity<JwtAuthResponse> login(LoginDto loginDto) throws Exception {
//        Authentication authentication ;
//        String token;
//
//        try{
//            Authentication auth =  new UsernamePasswordAuthenticationToken(
//                    loginDto.getEmail(),loginDto.getPassword());
//
//            authentication = authenticationManager.authenticate(auth);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            token = jwtTokenProvider.generateToken(authentication);
//            httpServletResponse.setHeader("Authorization", token);
//
//        }
//        catch (
//                BadCredentialsException ex){
//            throw new Exception("incorrect user credentials", ex);
//
//        }
//        return ResponseEntity.ok(new JwtAuthResponse(token));
//    }

    @Override
    public ResponseEntity<UserDto> login(LoginDto loginDto) throws Exception {
        Authentication authentication ;
        String token;

        try{
            Authentication auth =  new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(),loginDto.getPassword());

            authentication = authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenProvider.generateToken(authentication);
            log.info(token);
            httpServletResponse.setHeader("Authorization", token);
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> loggedInUser = userRepository.findByUsernameOrEmail(userName,userName);
            loggedInUser.orElseThrow().setConfirmationToken(token);
            Cart cart = loggedInUser.orElseThrow().getCart();
            CartDto cartDto = new CartDto();
            cartDto.setCartId(cart.getCartId());
            cartDto.setUserId(cart.getUser().getUserId());
            cartDto.setCartTotal(cart.getCartTotal());
            cartDto.setQuantity(cart.getQuantity());

            List<CartItemDto> cartItemDtoList =
                    cart.getCartItemList()
                            .stream()
                            .map(this::convertCartItemToDto)
                            .collect(Collectors.toList());
            cartDto.setCartItemList(cartItemDtoList);
            UserDto loggedInUserDto = new UserDto(
                    loggedInUser.orElseThrow().getFirstName(),
                    loggedInUser.orElseThrow().getLastName(),
                    loggedInUser.orElseThrow().getUsername(),
                    token,
                    loggedInUser.orElseThrow().getRoles(),
                    loggedInUser.orElseThrow().getGender(),
                    loggedInUser.orElseThrow().getUserId(),
                    cartDto,
                    loggedInUser.orElseThrow().getEmail()
                    );
//                    loggedInUser.orElseThrow().getGender().toString());
            System.out.println(loggedInUser.orElseThrow().getGender());
            return ResponseEntity.ok(loggedInUserDto);
        }
        catch (
                BadCredentialsException ex){
            throw new Exception("incorrect user credentials", ex);

        }

//        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

    @Override
    public ResponseEntity<?> logout(String token) {

        token = httpServletRequest.getHeader("Authorization");

        blackListService.blackListToken(token);

        return new ResponseEntity<>("Logout Successful", HttpStatus.OK);

    }

    private CartItemDto convertCartItemToDto(CartItem cartItem){
        CartItemDto cartItemDto = new CartItemDto();
        Product product = cartItem.getProduct();

        cartItemDto.setId(cartItem.getCartItemId());
        cartItemDto.setProductId(product.getProductId());
        cartItemDto.setProductName(product.getProductName());
        cartItemDto.setProductImage(product.getProductImage());
        cartItemDto.setProductSize(product.getSize());
        cartItemDto.setQuantity(cartItem.getQuantity());
        cartItemDto.setUnitPrice(product.getProductPrice());
        cartItemDto.setSubTotal(cartItem.getSubTotal());
        cartItemDto.setCartId(cartItem.getCart().getCartId());

        return cartItemDto;
    }


}

