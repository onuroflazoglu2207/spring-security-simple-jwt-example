package com.example.demo.service;

import com.example.demo.dto.DemoRequestDTO;
import com.example.demo.dto.DemoResponseDTO;
import com.example.demo.mapper.DemoRequestMapper;
import com.example.demo.mapper.DemoResponseMapper;
import com.example.demo.model.RequestModel;
import com.example.demo.model.ResponseModel;
import com.example.demo.model.Roles;
import com.example.demo.model.DemoModel;
import com.example.demo.repository.DemoRepository;
import com.example.demo.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DemoServiceImpl implements DemoService {

    private final DemoRepository repository;
    private final DemoResponseMapper responseMapper;
    private final DemoRequestMapper requestMapper;

    private final JwtTokenUtil util;

    @Override
    public ResponseEntity<?> authenticate(RequestModel request) {
        try {
            UserDetails details = getUserDetails(request.getUsername(), request.getPassword());
            if (details == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT Token unacceptable user!");
            String token = util.generateToken(details);
            return ResponseEntity.ok(new ResponseModel(token));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("JWT Token user is disable!");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("JWT Token user is invalid!");
        }
    }

    public UserDetails getUserDetails(String identity) {
        if (identity == null || !isNumber(identity)) return null;
        DemoModel model = getWithId(Long.valueOf(identity));
        if (model == null) return null;
        Collection<GrantedAuthority> roles = new HashSet<>();
        if (model.getRoles() == Roles.ADMIN) roles.add(new SimpleGrantedAuthority(Roles.ADMIN.name()));
        else if (model.getRoles() == Roles.USER) roles.add(new SimpleGrantedAuthority(Roles.USER.name()));
        else return null;
        return User.builder().username(identity).password(model.getPassword()).authorities(roles).build();
    }

    public UserDetails getUserDetails(String identity, String password) {
        UserDetails details = getUserDetails(identity);
        if (details == null || !details.getPassword().equals(password)) return null;
        else return details;
    }

    @Override
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(responseMapper.toDemoResponseDTOs(repository.findAll()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized!");
        String identity = authentication.getPrincipal().toString();
        if (identity == null || !isNumber(identity))
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Identity not found or unacceptable!");
        return getById(Long.valueOf(identity));
    }

    @Override
    public ResponseEntity<?> getById(Long identity) {
        DemoResponseDTO dto = responseMapper.toDemoResponseDTO(getWithId(identity));
        if (dto != null) return new ResponseEntity<>(dto, HttpStatus.OK);
        else return new ResponseEntity<>("Demo is not found!", HttpStatus.NOT_FOUND);
    }

    public DemoModel getWithId(Long identity) {
        Optional<DemoModel> optional = repository.findById(identity);
        return optional.isEmpty() ? null : optional.get();
    }

    @Override
    public ResponseEntity<?> save(DemoRequestDTO dto) {
        try {
            DemoModel model = requestMapper.toDemoModel(dto);
            model = repository.save(model);
            return new ResponseEntity<>(responseMapper.toDemoResponseDTO(model), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Have unacceptable field!", HttpStatus.CONFLICT);
        }
    }

    @Override
    public ResponseEntity<?> update(DemoRequestDTO dto, Long identity) {
        try {
            DemoModel model = getWithId(identity);
            if (model == null)
                return new ResponseEntity<>("Demo is not found!", HttpStatus.NOT_FOUND);
            DemoModel temp = requestMapper.toDemoModel(dto);
            temp.setIdentity(identity);
            repository.save(temp);
            return new ResponseEntity<>(responseMapper.toDemoResponseDTO(model), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Have unacceptable field!", HttpStatus.CONFLICT);
        }
    }

    @Override
    public ResponseEntity<?> changeRole(Roles roles, Long identity) {
        DemoModel model = getWithId(identity);
        if (model == null)
            return new ResponseEntity<>("Demo is not found!", HttpStatus.NOT_FOUND);
        if (model.getRoles() == Roles.USER || roles == Roles.ADMIN)
            return changeRole(model, roles);
        else {
            List<DemoModel> list = repository.findAll();
            long count = list.stream().filter(DemoModel -> DemoModel.getRoles() == Roles.ADMIN).count();
            if (count > 1) {
                return changeRole(model, roles);
            } else return new ResponseEntity<>("This Demo is last admin, so cant delete!", HttpStatus.BAD_GATEWAY);
        }
    }

    private ResponseEntity<?> changeRole(DemoModel model, Roles roles) {
        model.setRoles(roles);
        repository.save(model);
        return new ResponseEntity<>(responseMapper.toDemoResponseDTO(model), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> delete(Long identity) {
        DemoModel model = getWithId(identity);
        if (model != null) {
            if (model.getRoles() != Roles.ADMIN) {
                repository.deleteById(identity);
                return new ResponseEntity<>(responseMapper.toDemoResponseDTO(model), HttpStatus.OK);
            } else return new ResponseEntity<>("Cant delete admin!", HttpStatus.BAD_REQUEST);
        } else return new ResponseEntity<>("Demo is not found!", HttpStatus.NOT_FOUND);
    }

    public static boolean isNumber(String identity) {
        for (char a : identity.toCharArray())
            if (a < '0' || a > '9') return false;
        return true;
    }
}
