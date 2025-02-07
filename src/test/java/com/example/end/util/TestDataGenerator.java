//package com.example.end.util;
//
//import com.example.end.models.User;
//import com.github.javafaker.Faker;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Class name: TestDataGenerator
// * Description:
// *
// * @author Ganna Bieliaieva
// * @since 17/11/2024
// */
//public class TestDataGenerator {
//    private static final Faker faker = new Faker();
//
//    /**
//     * Generates a list of mock User objects with random data.
//     *
//     * @param count the number of User objects to generate.
//     * @return a List of mock User objects.
//     */
//    public static List<User> generateMockMasters(int count) {
//        List<User> users = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            User user = new User();
//            user.setId((long) i);
//            user.setFirstName(faker.name().fullName());
//            user.setEmail(faker.internet().emailAddress());
//            user.setRole(User.Role.MASTER);
//            user.setActive(faker.bool().bool());
//            users.add(user);
//        }
//        return users;
//    }
//}
