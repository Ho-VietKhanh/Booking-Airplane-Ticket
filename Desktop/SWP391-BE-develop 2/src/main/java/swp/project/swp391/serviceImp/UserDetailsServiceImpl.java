    package swp.project.swp391.serviceImp;

    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.stereotype.Service;
    import swp.project.swp391.entity.User;
    import swp.project.swp391.repository.UserRepository;
    import swp.project.swp391.exception.BaseException;
    import swp.project.swp391.constant.ErrorHandler;

    @Service
    @RequiredArgsConstructor
    public class UserDetailsServiceImpl implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BaseException(ErrorHandler.INVALID_CREDENTIALS));
            return user;
        }
    }
