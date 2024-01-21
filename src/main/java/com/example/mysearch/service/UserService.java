package com.example.mysearch.service;
import com.example.mysearch.model.User;
import com.example.mysearch.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Cette classe est responsable de la gestion des utilisateurs.
 * Elle implémente l'interface UserDetailsService de Spring Security.
 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructeur de la classe UserService.
     * 
     * @param userRepository   Le repository utilisé pour accéder aux données des utilisateurs.
     * @param passwordEncoder  L'encodeur de mot de passe utilisé pour sécuriser les mots de passe des utilisateurs.
     */
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Récupère un utilisateur par son identifiant.
     * 
     * @param id  L'identifiant de l'utilisateur.
     * @return    L'utilisateur correspondant à l'identifiant, ou null s'il n'existe pas.
     */
    public User getUserById(Long id) {
        return userRepository.findById(String.valueOf(id)).orElse(null);
    }

    /**
     * Crée un nouvel utilisateur.
     * Le mot de passe de l'utilisateur est encodé avant d'être enregistré.
     * 
     * @param user  L'utilisateur à créer.
     * @return      L'utilisateur créé.
     */
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Récupère un utilisateur par son identifiant.
     * 
     * @param id  L'identifiant de l'utilisateur.
     * @return    L'utilisateur correspondant à l'identifiant, ou null s'il n'existe pas.
     */
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Supprime un utilisateur par son identifiant.
     * 
     * @param id  L'identifiant de l'utilisateur à supprimer.
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(String.valueOf(id));
    }

    /**
     * Supprime un utilisateur par son identifiant.
     * 
     * @param id  L'identifiant de l'utilisateur à supprimer.
     */
    public void deleteUser(String id){
        userRepository.deleteById(id);
    }

    /**
     * Met à jour les informations d'un utilisateur.
     * Le mot de passe de l'utilisateur est encodé avant d'être enregistré.
     * 
     * @param id    L'identifiant de l'utilisateur à mettre à jour.
     * @param user  Les nouvelles informations de l'utilisateur.
     * @return      L'utilisateur mis à jour, ou null s'il n'existe pas.
     */
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

    /**
     * Récupère tous les utilisateurs.
     * 
     * @return  Une collection contenant tous les utilisateurs.
     */
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Charge les détails d'un utilisateur par son nom d'utilisateur.
     * 
     * @param username  Le nom d'utilisateur de l'utilisateur à charger.
     * @return          Les détails de l'utilisateur.
     * @throws UsernameNotFoundException  Si le nom d'utilisateur est vide ou si l'utilisateur n'est pas trouvé.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Le nom d'utilisateur est vide");
        }
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur : " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getName())
                .password(user.getPassword())
                .authorities("USER") // ou tout autre rôle que vous avez
                .build();
    }

    /**
     * Supprime un utilisateur de la base de données par son nom d'utilisateur.
     * 
     * @param username  Le nom d'utilisateur de l'utilisateur à supprimer.
     */
    public void removeUserFromDatabase(String username) {
        userRepository.deleteByName(username);
    }

    /**
     * Ajoute une recherche à l'historique d'un utilisateur.
     * 
     * @param name    Le nom d'utilisateur de l'utilisateur.
     * @param search  La recherche à ajouter à l'historique.
     */
    public void addSearchToHistory(String name,String search) {
        userRepository.addSearchToHistory(name,search);
    }

    /**
     * Récupère un utilisateur par son nom d'utilisateur.
     * 
     * @param username  Le nom d'utilisateur de l'utilisateur.
     * @return          L'utilisateur correspondant au nom d'utilisateur, ou null s'il n'existe pas.
     */
    public User getUserByUsername(String username) {
        return userRepository.findByName(username).orElse(null);
    }
}
