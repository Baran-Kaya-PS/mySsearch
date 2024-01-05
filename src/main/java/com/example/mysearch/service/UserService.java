package com.example.mysearch.service;
import com.example.mysearch.model.User;
import com.example.mysearch.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(Long id) {
        return userRepository.findById(String.valueOf(id)).orElse(null);
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(String.valueOf(id));
    }

    public User updateUser(Long id, User user) {
        User userToUpdate = userRepository.findById(String.valueOf(id)).orElse(null);
        if (userToUpdate != null) {
            userToUpdate.setName(user.getName());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
            userToUpdate.setHistoriqueRecherches(user.getHistoriqueRecherches());
            return userRepository.save(userToUpdate);
        }
        return null;
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
