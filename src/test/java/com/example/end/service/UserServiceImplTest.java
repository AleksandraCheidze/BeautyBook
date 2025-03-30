package com.example.end.service;

import com.example.end.dto.*;
import com.example.end.infrastructure.exceptions.ResourceNotFoundException;
import com.example.end.infrastructure.exceptions.RestException;
import com.example.end.infrastructure.exceptions.UserNotFoundException;
import com.example.end.infrastructure.mail.ProjectMailSender;
import com.example.end.mapping.UserMapper;
import com.example.end.models.Category;
import com.example.end.models.User;
import com.example.end.repository.CategoryRepository;
import com.example.end.repository.UserRepository;
import com.example.end.infrastructure.security.sec_servivce.TokenService;
import com.example.end.util.TestDataGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Class name: UserServiceImplTest
 * Description:
 *
 * @author Ganna Bieliaieva
 * @since 02/11/2024
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepositoryMocked;
    @Mock
    private CategoryRepository categoryRepositoryMocked;
    @Mock
    private UserMapper userMapperMocked;
    @Mock
    private PasswordEncoder passwordEncoderMocked;
    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Mock
    private SenderService senderServiceMocked;
    @Mock
    private ProjectMailSender mailSenderMocked;
    @Mock
    private TokenService tokenServiceMocked;
    @InjectMocks
    private UserServiceImpl userServiceMocked;

    private final User client = new User(1L, "clientFirstName", "clientLastName", "email@testClient.de", true, User.Role.CLIENT);
    private final UserDto userMasterDto = new UserDto(2L, "masterFirstName", "masterLastName", "hashPassword", "email@testMaster.de", User.Role.MASTER, "accessToken", "refreshToken");
    private final UserDto userClientDto = new UserDto(1L, "clientFirstName", "clientLastName", "hashPassword", "email@testClient.de", User.Role.CLIENT, "accessToken", "refreshToken");
    private final User master = new User(2L, "masterFirstName", "masterLastName", "email@testMaster.de", true, User.Role.MASTER);
    private NewUserDto newUserDto = new NewUserDto();
    private final UserDetailsDto userDetailsDto = new UserDetailsDto();
    private final NewUserDetailsDto newUserDetailsDto = new NewUserDetailsDto();
    private final UserDetailsDto userDetailsDto1 = new UserDetailsDto();
    private final String password = "password";
    private final String masterEmail = "email@testMaster.de";
    private final String clientEmail = "email@testClient.de";
    Long clientId = 1L;
    Long masterId = 2L;
    User.Role masterRole = User.Role.MASTER;
    User.Role clientRole = User.Role.CLIENT;

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    public class RegisterUser_Tests {

        @BeforeEach
        void setUp() {
            newUserDto = new NewUserDto();
            newUserDto.setEmail("email@testMaster.de");
            newUserDto.setRole(User.Role.MASTER);
            newUserDto.setHashPassword("password");
            newUserDto.setFirstName("UserFirstName");
            newUserDto.setLastName("UserLastName");
        }

        @Test
        void register_return_userDto_with_role_master_registered_successfully() {
            User savedMaster = new User();
            savedMaster.setEmail("email@testMaster.de");
            savedMaster.setRole(User.Role.MASTER);
            savedMaster.setFirstName("UserFirstName");
            savedMaster.setLastName("UserLastName");
            savedMaster.setHashPassword("hashedPassword");
            savedMaster.setActive(false);

            UserDto userMasterDto = new UserDto();
            userMasterDto.setEmail("email@testMaster.de");
            userMasterDto.setRole(User.Role.MASTER);
            userMasterDto.setAccessToken("accessToken");
            userMasterDto.setRefreshToken("refreshToken");

            when(userRepositoryMocked.existsByEmail(newUserDto.getEmail())).thenReturn(false);
            when(passwordEncoderMocked.encode(newUserDto.getHashPassword())).thenReturn("hashedPassword");
            when(userRepositoryMocked.save(any(User.class))).thenReturn(savedMaster);
            when(userMapperMocked.toDto(any(User.class))).thenReturn(userMasterDto);
            when(tokenServiceMocked.generateAccessToken(any(User.class))).thenReturn("accessToken");
            when(tokenServiceMocked.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

            UserDto userDto = userServiceMocked.register(newUserDto);

            verify(userRepositoryMocked, times(1)).existsByEmail(newUserDto.getEmail());
            verify(passwordEncoderMocked, times(1)).encode(newUserDto.getHashPassword());

            verify(senderServiceMocked, times(1)).sendMasterRegistrationConfirmation(userCaptor.capture());
            User capturedUser = userCaptor.getValue();

            assertEquals(newUserDto.getEmail(), capturedUser.getEmail());
            assertEquals(newUserDto.getRole(), capturedUser.getRole());
            assertEquals(newUserDto.getFirstName(), capturedUser.getFirstName());
            assertEquals(newUserDto.getLastName(), capturedUser.getLastName());
            assertEquals("hashedPassword", capturedUser.getHashPassword());
            assertFalse(capturedUser.isActive());

            verify(mailSenderMocked, never()).sendRegistrationEmail(any());
            verify(userRepositoryMocked, times(1)).save(any(User.class));
            verify(userMapperMocked, times(1)).toDto(any(User.class));
            verify(tokenServiceMocked, times(1)).generateAccessToken(any(User.class));
            verify(tokenServiceMocked, times(1)).generateRefreshToken(any(User.class));

            assertEquals(userMasterDto.getRole(), userDto.getRole());
            assertEquals(userMasterDto.getEmail(), userDto.getEmail());
            assertNotNull(userDto.getAccessToken());
            assertNotNull(userDto.getRefreshToken());
        }

        @Test
        void register_return_userDto_with_role_client_registered_successfully() {
            newUserDto.setEmail(clientEmail);
            newUserDto.setRole(User.Role.CLIENT);

            client.setHashPassword("hashedPassword");

            when(userRepositoryMocked.existsByEmail(clientEmail)).thenReturn(false);
            when(passwordEncoderMocked.encode("password")).thenReturn("hashedPassword");
            when(userRepositoryMocked.save(any(User.class))).thenReturn(client);
            when(userMapperMocked.toDto(client)).thenReturn(userClientDto);
            when(tokenServiceMocked.generateAccessToken(client)).thenReturn("accessToken");
            when(tokenServiceMocked.generateRefreshToken(client)).thenReturn("refreshToken");

            UserDto userDto = userServiceMocked.register(newUserDto);

            verify(userRepositoryMocked, times(1)).existsByEmail(clientEmail);
            verify(passwordEncoderMocked, times(1)).encode("password");

            verify(mailSenderMocked, times(1)).sendRegistrationEmail(clientEmail);
            verify(senderServiceMocked, never()).sendMasterRegistrationConfirmation(any());

            verify(userRepositoryMocked, times(1)).save(any(User.class));
            verify(userMapperMocked, times(1)).toDto(any(User.class));
            verify(tokenServiceMocked, times(1)).generateAccessToken(client);
            verify(tokenServiceMocked, times(1)).generateRefreshToken(client);

            assertEquals(userClientDto.getRole(), userDto.getRole());
            assertEquals(userClientDto.getEmail(), userDto.getEmail());
            assertTrue(client.isActive());
        }

        @Test
        public void register_User_throws_RestException_Conflict() {
            newUserDto.setEmail(masterEmail);
            when(userRepositoryMocked.existsByEmail(masterEmail)).thenReturn(true);

            RestException e = assertThrows(RestException.class, () -> userServiceMocked.register(newUserDto));
            assertEquals(HttpStatus.CONFLICT, e.getStatus());
            assertEquals("User with email <email@testMaster.de> already exists", e.getMessage());

            verify(userRepositoryMocked, times(1)).existsByEmail(masterEmail);
            verify(passwordEncoderMocked, never()).encode(any());
            verify(userRepositoryMocked, never()).save(any(User.class));
            verify(userMapperMocked, never()).toDto(any());
            verify(mailSenderMocked, never()).sendEmail(any(), any(), any());
            verify(tokenServiceMocked, never()).generateAccessToken(any());
            verify(tokenServiceMocked, never()).generateRefreshToken(any());
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class Authenticate_Tests {
        @Test
        void authenticate_return_userDto_successful() {
            client.setHashPassword("hashedPassword");

            when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.of(client));
            when(passwordEncoderMocked.matches(password, "hashedPassword")).thenReturn(true);
            when(userMapperMocked.toDto(client)).thenReturn(userClientDto);

            UserDto userDto = userServiceMocked.authenticate(clientEmail, password);
            verify(userRepositoryMocked, times(1)).findByEmail(clientEmail);
            verify(passwordEncoderMocked, times(1)).matches(password, "hashedPassword");
            verify(userMapperMocked, times(1)).toDto(client);
            assertEquals(userClientDto, userDto);
        }

        @Test
        void authenticate_throws_UserNotFoundException_incorrect_email() {
            when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.empty());

            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> userServiceMocked.authenticate(clientEmail, password));

            assertEquals("User not found for email: " + clientEmail, e.getMessage());

            verify(userRepositoryMocked, times(1)).findByEmail(clientEmail);
            verify(passwordEncoderMocked, never()).matches(any(), any());
            verify(userMapperMocked, never()).toDto(any(User.class));
        }

        @Test
        void authenticate_throws_RestException_incorrect_password() {
            client.setHashPassword("hashedPassword");
            when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.of(client));
            when(passwordEncoderMocked.matches(password, "hashedPassword")).thenReturn(false);

            RestException e = assertThrows(RestException.class,
                    () -> userServiceMocked.authenticate(clientEmail, password));

            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals("Invalid password", e.getMessage());
            verify(userRepositoryMocked, times(1)).findByEmail(clientEmail);
            verify(passwordEncoderMocked, times(1)).matches(password, "hashedPassword");
            verify(userMapperMocked, never()).toDto(any(User.class));
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class GetById_Tests {

        @Test
        void getById_return_user_successful() {
            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.of(client));
            when(userMapperMocked.userDetailsToDto(client)).thenReturn(userDetailsDto);

            UserDetailsDto result = userServiceMocked.getById(clientId);

            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userMapperMocked, times(1)).userDetailsToDto(client);
            assertEquals(userDetailsDto, result);
        }

        @Test
        void getById_throws_UserNotFoundException_User_not_found() {
            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.empty());

            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> userServiceMocked.getById(clientId));

            assertEquals("User not found for id: 1", e.getMessage());
            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userMapperMocked, never()).userDetailsToDto(any(User.class));
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class ValidateEmail_Tests {
        @Test
        void validateEmail_email_was_validated_successfully() {
            when(userRepositoryMocked.existsByEmail(clientEmail)).thenReturn(false);

            userServiceMocked.validateEmail(clientEmail);

            verify(userRepositoryMocked, times(1)).existsByEmail(clientEmail);
        }

        @Test
        void validateEmail_throws_RestException() {
            when(userRepositoryMocked.existsByEmail(clientEmail)).thenReturn(true);

            RestException e = assertThrows(RestException.class,
                    () -> userServiceMocked.validateEmail(clientEmail));

            assertEquals(HttpStatus.CONFLICT, e.getStatus());
            assertEquals("User with email <email@testClient.de> already exists", e.getMessage());
            verify(userRepositoryMocked, times(1)).existsByEmail(clientEmail);
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class UpdateUserDetails {
        @Test
        void updateUserDetails_throws_UserNotFoundException_users_not_exist() {
            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.empty());

            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> userServiceMocked.updateUserDetails(clientId, newUserDetailsDto));

            assertEquals("User not found for id: 1", e.getMessage());
            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(categoryRepositoryMocked, never()).findAllById(any());
            verify(userRepositoryMocked, never()).save(any());
            verify(userMapperMocked, never()).userDetailsToDto(any());
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class GetMasterById_Tests {

        @Test
        void getMasterById_return_user() {
            when(userRepositoryMocked.findByIdAndRole(masterId, masterRole)).thenReturn(Optional.of(master));
            when(userMapperMocked.toDto(master)).thenReturn(userMasterDto);

            UserDto result = userServiceMocked.getMasterById(masterId);

            verify(userRepositoryMocked, times(1)).findByIdAndRole(masterId, masterRole);
            verify(userMapperMocked, times(1)).toDto(master);
            assertEquals(userMasterDto, result);
        }

        @Test
        void getMasterById_throws_UserNotFoundException_User_not_found() {
            when(userRepositoryMocked.findByIdAndRole(masterId, masterRole)).thenReturn(Optional.empty());

            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> userServiceMocked.getMasterById(masterId));

            assertEquals("User not found for id: 2 with role: MASTER", e.getMessage());
            verify(userRepositoryMocked, times(1)).findByIdAndRole(masterId, masterRole);
            verify(userMapperMocked, never()).toDto(any(User.class));
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class GetClientById_Tests {

        @Test
        void getClientById_return_user() {
            when(userRepositoryMocked.findByIdAndRole(clientId, clientRole)).thenReturn(Optional.of(client));
            when(userMapperMocked.toDto(client)).thenReturn(userClientDto);

            UserDto result = userServiceMocked.getClientById(clientId);

            verify(userRepositoryMocked, times(1)).findByIdAndRole(clientId, clientRole);
            verify(userMapperMocked, times(1)).toDto(client);
            assertEquals(userClientDto, result);
        }

        @Test
        void getClientById_throws_UserNotFoundException_User_not_found() {
            when(userRepositoryMocked.findByIdAndRole(clientId, clientRole)).thenReturn(Optional.empty());

            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> userServiceMocked.getClientById(clientId));

            assertEquals("User not found for id: 1 with role: CLIENT", e.getMessage());
            verify(userRepositoryMocked, times(1)).findByIdAndRole(clientId, clientRole);
            verify(userMapperMocked, never()).toDto(any(User.class));
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class ConfirmMasterByEmail_Tests {
        @Test
        void confirmMasterByEmail_activate_and_send_email() {
            master.setActive(false);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            userServiceMocked.confirmMasterByEmail(masterEmail);

            verify(mailSenderMocked, times(1)).sendRegistrationEmail(masterEmail);
            verify(userRepositoryMocked, times(1)).save(master);
            assertTrue(master.isActive());
        }

        @Test
        void confirmMasterByEmail_throws_UserNotFoundException_master_by_wrong_email_not_found() {
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.empty());

            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> userServiceMocked.confirmMasterByEmail(masterEmail));

            assertEquals("User not found for email: email@testMaster.de", e.getMessage());
            verify(mailSenderMocked, never()).sendRegistrationEmail(any());
            verify(userRepositoryMocked, never()).save(any());
        }

        @Test
        void confirmMasterByEmail_throws_UserNotFoundException_master_by_wrong_role_not_found() {
            master.setRole(User.Role.CLIENT);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            UserNotFoundException e = assertThrows(UserNotFoundException.class,
                    () -> userServiceMocked.confirmMasterByEmail(masterEmail));

            assertEquals("User with email email@testMaster.de is not a MASTER.", e.getMessage());
            verify(mailSenderMocked, never()).sendRegistrationEmail(any());
            verify(userRepositoryMocked, never()).save(any());
        }

        @Test
        void confirmMasterByEmail_throws_UserNotFoundException_master_has_been_already_confirmed() {
            master.setActive(true);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            UserNotFoundException e = assertThrows(UserNotFoundException.class,
                    () -> userServiceMocked.confirmMasterByEmail(masterEmail));

            assertEquals("Master user with email email@testMaster.de is already active.", e.getMessage());
            verify(mailSenderMocked, never()).sendRegistrationEmail(any());
            verify(userRepositoryMocked, never()).save(any());
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class FindMasterUserByEmail_Tests {
        @Test
        void findMasterUserByEmail_return_user_successful() {
            master.setActive(false);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            User result = userServiceMocked.findMasterUserByEmail(masterEmail);

            verify(userRepositoryMocked, times(1)).findByEmail(masterEmail);
            assertEquals(master, result);
        }

        @Test
        void findMasterUserByEmail_throws_UserNotFoundException_master_by_wrong_email_not_found() {
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.empty());

            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> userServiceMocked.findMasterUserByEmail(masterEmail));

            assertEquals("User not found for email: " + masterEmail, e.getMessage());
            verify(userRepositoryMocked, times(1)).findByEmail(masterEmail);
        }

        @Test
        void findMasterUserByEmail_throws_UserNotFoundException_master_by_wrong_role_not_found() {
            master.setRole(User.Role.CLIENT);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            UserNotFoundException e = assertThrows(UserNotFoundException.class,
                    () -> userServiceMocked.findMasterUserByEmail(masterEmail));

            assertEquals("User with email " + masterEmail + " is not a MASTER.", e.getMessage());
            verify(userRepositoryMocked, times(1)).findByEmail(masterEmail);
        }

        @Test
        void findMasterUserByEmail_throws_UserNotFoundException_master_is_already_active() {
            master.setActive(true);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            UserNotFoundException e = assertThrows(UserNotFoundException.class,
                    () -> userServiceMocked.findMasterUserByEmail(masterEmail));

            assertEquals("Master user with email " + masterEmail + " is already active.", e.getMessage());
            verify(userRepositoryMocked, times(1)).findByEmail(masterEmail);
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class GetAllMasters_Tests {
        @Test
        void getAllMasters_return_list_of_users_with_role_master_successful() {
            List<User> masters = List.of(master, master);
            when(userRepositoryMocked.findAllByRole(masterRole)).thenReturn(masters);
            when(userMapperMocked.userDetailsToDto(master)).thenReturn(userDetailsDto);

            List<UserDetailsDto> result = userServiceMocked.getAllMasters();

            verify(userRepositoryMocked, times(1)).findAllByRole(masterRole);
            verify(userMapperMocked, times(2)).userDetailsToDto(master);
            assertEquals(2, result.size());
            assertEquals(userDetailsDto, result.get(0));
            assertEquals(userDetailsDto, result.get(1));
        }

        @Test
        void getAllMasters_return_empty_list_of_users_with_role_master_successful() {
            when(userRepositoryMocked.findAllByRole(masterRole)).thenReturn(Collections.emptyList());

            List<UserDetailsDto> result = userServiceMocked.getAllMasters();

            verify(userRepositoryMocked, times(1)).findAllByRole(masterRole);
            verify(userMapperMocked, never()).userDetailsToDto(any());
            assertTrue(result.isEmpty());
        }

        @Test
        void getAllMasters_return_big_list_of_users_with_role_master_successful() {
            int countUsers = 10000;
            List<User> masters = TestDataGenerator.generateMockMasters(countUsers);

            when(userRepositoryMocked.findAllByRole(masterRole)).thenReturn(masters);
            when(userMapperMocked.userDetailsToDto(any())).thenReturn(new UserDetailsDto());

            List<UserDetailsDto> result = userServiceMocked.getAllMasters();

            verify(userRepositoryMocked, times(1)).findAllByRole(masterRole);
            verify(userMapperMocked, times(countUsers)).userDetailsToDto(any());
            assertEquals(countUsers, result.size());
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class GetAllUsers_Tests {
        @Test
        void getAllUsers_return_list_of_users_successful() {
            List<User> users = List.of(client, master);
            when(userRepositoryMocked.findAll()).thenReturn(users);
            when(userMapperMocked.userDetailsToDto(client)).thenReturn(userDetailsDto1);
            when(userMapperMocked.userDetailsToDto(master)).thenReturn(userDetailsDto);

            List<UserDetailsDto> result = userServiceMocked.getAllUsers();

            verify(userRepositoryMocked, times(1)).findAll();
            verify(userMapperMocked, times(1)).userDetailsToDto(client);
            verify(userMapperMocked, times(1)).userDetailsToDto(master);
            assertEquals(2, result.size());
            assertEquals(userDetailsDto1, result.get(0));
            assertEquals(userDetailsDto, result.get(1));
        }

        @Test
        void getAllUsers_return_empty_list_of_users_successful() {
            when(userRepositoryMocked.findAll()).thenReturn(Collections.emptyList());

            List<UserDetailsDto> result = userServiceMocked.getAllUsers();

            verify(userRepositoryMocked, times(1)).findAll();
            verify(userMapperMocked, never()).userDetailsToDto(any());
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class FindUsersByCategoryId_Tests {
        @Test
        void findUsersByCategoryId_return_list_of_users_successful_with_each_has_category() {
            Long categoryId = 2L;
            when(categoryRepositoryMocked.existsById(categoryId)).thenReturn(true);
            when(userRepositoryMocked.findUsersByCategoryId(categoryId)).thenReturn(List.of(client, master));
            when(userMapperMocked.userDetailsToDto(client)).thenReturn(userDetailsDto1);
            when(userMapperMocked.userDetailsToDto(master)).thenReturn(userDetailsDto);

            List<UserDetailsDto> result = userServiceMocked.findUsersByCategoryId(categoryId);

            verify(categoryRepositoryMocked).existsById(categoryId);
            verify(userRepositoryMocked).findUsersByCategoryId(categoryId);
            verify(userMapperMocked).userDetailsToDto(client);
            verify(userMapperMocked).userDetailsToDto(master);
            assertEquals(2, result.size());
            assertEquals(userDetailsDto1, result.get(0));
            assertEquals(userDetailsDto, result.get(1));
        }

        @Test
        void findUsersByCategoryId_throws_ResourceNotFoundException_when_category_not_found() {
            Long nonExistentCategoryId = 999L;
            when(categoryRepositoryMocked.existsById(nonExistentCategoryId)).thenReturn(false);

            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> userServiceMocked.findUsersByCategoryId(nonExistentCategoryId));

            assertEquals("Category with ID 999 not found", e.getMessage());
            verify(categoryRepositoryMocked).existsById(nonExistentCategoryId);
            verify(userRepositoryMocked, never()).findUsersByCategoryId(any());
        }

        @Test
        void findUsersByCategoryId_return_list_of_users_successful_with_one_has_category() {
            when(categoryRepositoryMocked.existsById(1L)).thenReturn(true);
            when(userRepositoryMocked.findUsersByCategoryId(1L)).thenReturn(List.of(client));
            when(userMapperMocked.userDetailsToDto(client)).thenReturn(userDetailsDto1);

            List<UserDetailsDto> result = userServiceMocked.findUsersByCategoryId(1L);

            verify(userRepositoryMocked, times(1)).findUsersByCategoryId(1L);
            verify(userMapperMocked, times(1)).userDetailsToDto(client);
            assertEquals(1, result.size());
            assertEquals(userDetailsDto1, result.get(0));
        }

        @Test
        void findUsersByCategoryId_throws_UserNotFoundException_when_list_of_users_is_empty() {
            Long categoryId = 1L;
            when(categoryRepositoryMocked.existsById(categoryId)).thenReturn(true);
            when(userRepositoryMocked.findUsersByCategoryId(categoryId)).thenReturn(Collections.emptyList());

            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> userServiceMocked.findUsersByCategoryId(categoryId));

            assertEquals("User for category with ID 1 not found.", e.getMessage().trim());
            verify(categoryRepositoryMocked).existsById(categoryId);
            verify(userRepositoryMocked).findUsersByCategoryId(categoryId);
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class FindByEmail_Tests {
        @Test
        void findByEmail_return_User_successful() {
            when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.of(client));

            Optional<User> result = userServiceMocked.findByEmail(clientEmail);

            verify(userRepositoryMocked, times(1)).findByEmail(clientEmail);
            assertTrue(result.isPresent());
            assertEquals(client, result.get());
        }

        @Test
        void findByEmail_return_empty_Optional_User_with_this_Email_not_exist() {
            String notExistEmail = "email@notExistClient.de";
            when(userRepositoryMocked.findByEmail(notExistEmail)).thenReturn(Optional.empty());

            Optional<User> result = userServiceMocked.findByEmail(notExistEmail);

            verify(userRepositoryMocked, times(1)).findByEmail(notExistEmail);
            assertFalse(result.isPresent());
        }

        @Test
        void findByEmail_throws_IllegalArgumentException_when_email_is_null() {
            assertThrows(IllegalArgumentException.class,
                    () -> userServiceMocked.findByEmail(null));

            verify(userRepositoryMocked, never()).findByEmail(any());
        }

        @Test
        void findByEmail_throws_IllegalArgumentException_when_email_is_empty() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                    () -> userServiceMocked.findByEmail(""));

            assertEquals("Email must not be null or empty", e.getMessage());
            verify(userRepositoryMocked, never()).findByEmail(any());
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class DeleteById_Tests {
        @Test
        void deleteById_user_deleted_successful() {
            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.of(client));


            userServiceMocked.deleteById(clientId);

            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userRepositoryMocked, times(1)).delete(client);
        }

        @Test
        void deleteById_throws_UserNotFoundException_users_not_exist() {
            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.empty());

            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> userServiceMocked.deleteById(clientId));

            assertEquals("User not found for id: 1", e.getMessage());
            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userRepositoryMocked, never()).delete(any());
        }
    }
}