//package com.elice.nbbang;
//
//import com.elice.nbbang.domain.user.entity.User;
//import com.elice.nbbang.domain.user.entity.UserRole;
//import com.elice.nbbang.domain.user.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 유저 데이터가 존재하지 않을 경우 초기 데이터 삽입
//        if (userRepository.count() == 0) {
//            User user1 = User.builder()
//                    .nickname("user1")
//                    .email("user1@example.com")
//                    .password(bCryptPasswordEncoder.encode("123"))
//                    .phoneNumber("010-1234-5678")
//                    .role(UserRole.ROLE_USER)
//                    .build();
//
//            User user2 = User.builder()
//                    .nickname("user2")
//                    .email("user2@example.com")
//                    .password(bCryptPasswordEncoder.encode("123"))
//                    .phoneNumber("010-8765-4321")
//                    .role(UserRole.ROLE_USER)
//                    .build();
//
//            User admin = User.builder()
//                    .nickname("admin")
//                    .email("admin@example.com")
//                    .password(bCryptPasswordEncoder.encode("123"))
//                    .phoneNumber("010-0000-0000")
//                    .role(UserRole.ROLE_ADMIN)
//                    .build();
//
//            userRepository.save(user1);
//            userRepository.save(user2);
//            userRepository.save(admin);
//        }
//    }
//}
//
