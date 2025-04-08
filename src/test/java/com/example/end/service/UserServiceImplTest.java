//package com.example.end.service;
//
//import com.example.end.dto.*;
//import com.example.end.infrastructure.exceptions.ResourceNotFoundException;
//import com.example.end.infrastructure.exceptions.RestException;
//import com.example.end.infrastructure.mail.ProjectMailSender;
//import com.example.end.mapping.UserMapper;
//import com.example.end.models.*;
//import com.example.end.repository.CategoryRepository;
//import com.example.end.repository.UserRepository;
//import com.example.end.infrastructure.security.sec_servivce.TokenService;
//import com.example.end.service.interfaces.CategoryService;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.api.Nested;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("UserService Tests")
//public class UserServiceImplTest {
//
//    @Mock
//    private UserRepository userRepositoryMocked;
//    @Mock
//    private CategoryRepository categoryRepositoryMocked;
//    @Mock
//    private UserMapper userMapperMocked;
//    @Mock
//    private PasswordEncoder passwordEncoderMocked;
//    @Captor
//    private ArgumentCaptor<User> userCaptor;
//    @Mock
//    private SenderService senderServiceMocked;
//    @Mock
//    private ProjectMailSender mailSenderMocked;
//    @Mock
//    private TokenService tokenServiceMocked;
//    @InjectMocks
//    private UserServiceImpl userServiceMocked;
//    @Mock
//    private CategoryService categoryServiceMocked;
//
//    private final User client = new User(1L, "clientFirstName", "clientLastName", "email@testClient.de", true, User.Role.CLIENT);
//    private final UserDto userMasterDto = new UserDto(2L, "masterFirstName", "masterLastName", "password", "email@testMaster.de", User.Role.MASTER);
//    private final UserDto userClientDto = new UserDto(1L, "clientFirstName", "clientLastName", "password", "email@testClient.de", User.Role.CLIENT);
//    private final User master = new User(2L, "masterFirstName", "masterLastName", "email@testMaster.de", false, User.Role.MASTER);
//    private NewUserDto newUserDto = new NewUserDto();
//    private final UserDetailsDto userDetailsDto = new UserDetailsDto();
//    private final NewUserDetailsDto newUserDetailsDto = new NewUserDetailsDto();
//    private final UserDetailsDto userDetailsDto1 = new UserDetailsDto();
//    private final String password = "password";
//    private final String masterEmail = "email@testMaster.de";
//    private final String clientEmail = "email@testClient.de";
//    Long clientId = 1L;
//    Long masterId = 2L;
//    User.Role masterRole = User.Role.MASTER;
//    User.Role clientRole = User.Role.CLIENT;
//
//    @Nested
//    @DisplayNameGeneration(ReplaceUnderscores.class)
//    class RegisterUser_Tests {
//
//        @BeforeEach
//        void setUp() {
//            newUserDto = new NewUserDto();
//            newUserDto.setEmail("email@testMaster.de");
//            newUserDto.setRole(User.Role.MASTER);
//            newUserDto.setPassword("password");
//            newUserDto.setFirstName("UserFirstName");
//            newUserDto.setLastName("UserLastName");
//        }
//
//        @Test
//        void register_master_user_successfully() {
//            User savedMaster = new User();
//            savedMaster.setEmail("email@testMaster.de");
//            savedMaster.setRole(User.Role.MASTER);
//            savedMaster.setFirstName("UserFirstName");
//            savedMaster.setLastName("UserLastName");
//            savedMaster.setPassword("password");
//            savedMaster.setActive(false);
//
//            UserDto userMasterDto = new UserDto();
//            userMasterDto.setEmail("email@testMaster.de");
//            userMasterDto.setRole(User.Role.MASTER);
//
//            when(userRepositoryMocked.existsByEmail(newUserDto.getEmail())).thenReturn(false);
//            when(passwordEncoderMocked.encode(newUserDto.getPassword())).thenReturn("password");
//            when(userRepositoryMocked.save(any(User.class))).thenReturn(savedMaster);
//            when(userMapperMocked.toDto(any(User.class))).thenReturn(userMasterDto);
//            when(tokenServiceMocked.generateAccessToken(any(User.class))).thenReturn("accessToken");
//            when(tokenServiceMocked.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
//
//            UserDto result = userServiceMocked.register(newUserDto);
//
//            verify(userRepositoryMocked).existsByEmail(newUserDto.getEmail());
//            verify(passwordEncoderMocked).encode(newUserDto.getPassword());
//            verify(senderServiceMocked).sendMasterRegistrationConfirmation(userCaptor.capture());
//            verify(userRepositoryMocked).save(any(User.class));
//            verify(tokenServiceMocked).generateAccessToken(any(User.class));
//            verify(tokenServiceMocked).generateRefreshToken(any(User.class));
//
//            User capturedUser = userCaptor.getValue();
//            assertEquals(newUserDto.getEmail(), capturedUser.getEmail());
//            assertEquals(User.Role.MASTER, capturedUser.getRole());
//            assertFalse(capturedUser.isActive());
//            assertEquals(userMasterDto.getRole(), result.getRole());
//        }
//
//        @Test
//        void register_client_user_successfully() {
//            newUserDto.setRole(User.Role.CLIENT);
//            newUserDto.setEmail(clientEmail);
//
//            when(userRepositoryMocked.existsByEmail(clientEmail)).thenReturn(false);
//            when(passwordEncoderMocked.encode("password")).thenReturn("password");
//            when(userRepositoryMocked.save(any(User.class))).thenReturn(client);
//            when(userMapperMocked.toDto(client)).thenReturn(userClientDto);
//
//            UserDto result = userServiceMocked.register(newUserDto);
//
//            verify(userRepositoryMocked).existsByEmail(clientEmail);
//            verify(passwordEncoderMocked).encode("password");
//            verify(mailSenderMocked).sendRegistrationEmail(clientEmail);
//            verify(userRepositoryMocked).save(any(User.class));
//            assertTrue(client.isActive());
//            assertEquals(userClientDto.getRole(), result.getRole());
//        }
//
//        @Test
//        void register_throws_exception_when_email_exists() {
//            when(userRepositoryMocked.existsByEmail(masterEmail)).thenReturn(true);
//
//            RestException e = assertThrows(RestException.class,
//                    () -> userServiceMocked.register(newUserDto));
//
//            assertEquals(HttpStatus.CONFLICT, e.getStatus());
//            assertEquals("User with email <email@testMaster.de> already exists", e.getMessage());
//            verify(userRepositoryMocked).existsByEmail(masterEmail);
//            verify(passwordEncoderMocked, never()).encode(any());
//        }
//    }
//
//    @Nested
//    @DisplayNameGeneration(ReplaceUnderscores.class)
//    class Authenticate_Tests {
//        @Test
//        void authenticate_successfully() {
//            client.setPassword(password);
//
//            when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.of(client));
//            when(passwordEncoderMocked.matches(password, password)).thenReturn(true);
//            when(userMapperMocked.toDto(client)).thenReturn(userClientDto);
//
//            UserDto result = userServiceMocked.authenticate(clientEmail, password);
//
//            verify(userRepositoryMocked).findByEmail(clientEmail);
//            verify(passwordEncoderMocked).matches(password, password);
//            assertEquals(userClientDto, result);
//        }
//
//        @Test
//        void authenticate_throws_when_user_not_found() {
//            when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.empty());
//
//            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
//                    () -> userServiceMocked.authenticate(clientEmail, password));
//
//            assertEquals("User not found for email: " + clientEmail, e.getMessage());
//        }
//
//        @Test
//        void authenticate_throws_when_password_invalid() {
//            client.setPassword("encodedPassword");
//
//            when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.of(client));
//            when(passwordEncoderMocked.matches(any(), any())).thenReturn(false);
//
//            RestException e = assertThrows(RestException.class,
//                    () -> userServiceMocked.authenticate(clientEmail, password));
//
//            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
//            assertEquals("Invalid password", e.getMessage());
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class GetById_Tests {
//            @Test
//            void getById_successfully() {
//                when(userRepositoryMocked.findByIdWithDetails(clientId)).thenReturn(Optional.of(client));
//                when(userMapperMocked.userDetailsToDto(client)).thenReturn(userDetailsDto);
//
//                UserDetailsDto result = userServiceMocked.getById(clientId);
//
//                verify(userRepositoryMocked).findByIdWithDetails(clientId);
//                assertEquals(userDetailsDto, result);
//            }
//
//            @Test
//            void getById_throws_when_user_not_found() {
//                when(userRepositoryMocked.findByIdWithDetails(clientId)).thenReturn(Optional.empty());
//
//                ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
//                        () -> userServiceMocked.getById(clientId));
//
//                assertEquals("User not found with ID: " + clientId, e.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class ValidateEmail_Tests {
//            @Test
//            void validateEmail_successfully() {
//                when(userRepositoryMocked.existsByEmail(clientEmail)).thenReturn(false);
//                userServiceMocked.validateEmail(clientEmail);
//                verify(userRepositoryMocked).existsByEmail(clientEmail);
//            }
//
//            @Test
//            void validateEmail_throws_when_email_exists() {
//                when(userRepositoryMocked.existsByEmail(clientEmail)).thenReturn(true);
//
//                RestException e = assertThrows(RestException.class,
//                        () -> userServiceMocked.validateEmail(clientEmail));
//
//                assertEquals(HttpStatus.CONFLICT, e.getStatus());
//                assertEquals("User with email <email@testClient.de> already exists", e.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class UpdateUserDetails_Tests {
//            @Test
//            void updateUserDetails_successfully() {
//
//                NewUserDetailsDto detailsDto = new NewUserDetailsDto();
//                detailsDto.setDescription("New desc");
//                detailsDto.setPhoneNumber("123");
//                detailsDto.setAddress("Address");
//                detailsDto.setCategoryIds(List.of(1L));
//                detailsDto.setProcedureIds(List.of(1L));
//
//                Category category = new Category();
//                category.setId(1L);
//                category.setName("Category");
//
//                Procedure procedure = new Procedure();
//                procedure.setId(1L);
//                procedure.setName("Procedure");
//                procedure.setPrice(100);
//                procedure.setCategory(category);
//
//                Set<Category> categories = Set.of(category);
//                Set<Procedure> procedures = Set.of(procedure);
//
//                when(userRepositoryMocked.findByIdWithDetails(clientId)).thenReturn(Optional.of(client));
//                when(categoryRepositoryMocked.findAllById(detailsDto.getCategoryIds())).thenReturn(List.of(category));
//                when(categoryServiceMocked.getProceduresForCategories(categories, detailsDto.getProcedureIds()))
//                        .thenReturn(procedures);
//                when(userRepositoryMocked.save(client)).thenReturn(client);
//                when(userMapperMocked.userDetailsToDto(client)).thenReturn(userDetailsDto);
//
//                UserDetailsDto result = userServiceMocked.updateUserDetails(clientId, detailsDto);
//
//                verify(userRepositoryMocked).findByIdWithDetails(clientId);
//                verify(categoryRepositoryMocked).findAllById(detailsDto.getCategoryIds());
//                verify(categoryServiceMocked).getProceduresForCategories(categories, detailsDto.getProcedureIds());
//                verify(userRepositoryMocked).save(client);
//                assertEquals(userDetailsDto, result);
//            }
//
//            @Test
//            void updateUserDetails_throws_when_user_not_found() {
//                when(userRepositoryMocked.findByIdWithDetails(clientId)).thenReturn(Optional.empty());
//
//                ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
//                        () -> userServiceMocked.updateUserDetails(clientId, newUserDetailsDto));
//
//                assertEquals("User not found with ID: " + clientId, e.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class GetMasterById_Tests {
//            @Test
//            void getMasterById_successfully() {
//                when(userRepositoryMocked.findByIdAndRole(masterId, masterRole)).thenReturn(Optional.of(master));
//                when(userMapperMocked.toDto(master)).thenReturn(userMasterDto);
//
//                UserDto result = userServiceMocked.getMasterById(masterId);
//
//                verify(userRepositoryMocked).findByIdAndRole(masterId, masterRole);
//                assertEquals(userMasterDto, result);
//            }
//
//            @Test
//            void getMasterById_throws_when_not_found() {
//                when(userRepositoryMocked.findByIdAndRole(masterId, masterRole)).thenReturn(Optional.empty());
//
//                ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
//                        () -> userServiceMocked.getMasterById(masterId));
//
//                assertEquals("User not found for id: 2 with role: MASTER", e.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class GetClientById_Tests {
//            @Test
//            void getClientById_successfully() {
//                when(userRepositoryMocked.findByIdAndRole(clientId, clientRole)).thenReturn(Optional.of(client));
//                when(userMapperMocked.toDto(client)).thenReturn(userClientDto);
//
//                UserDto result = userServiceMocked.getClientById(clientId);
//
//                verify(userRepositoryMocked).findByIdAndRole(clientId, clientRole);
//                assertEquals(userClientDto, result);
//            }
//
//            @Test
//            void getClientById_throws_when_not_found() {
//                when(userRepositoryMocked.findByIdAndRole(clientId, clientRole)).thenReturn(Optional.empty());
//
//                ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
//                        () -> userServiceMocked.getClientById(clientId));
//
//                assertEquals("User not found for id: 1 with role: CLIENT", e.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class ConfirmMasterByEmail_Tests {
//            @Test
//            void confirmMasterByEmail_successfully() {
//                master.setActive(false);
//                when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));
//
//                userServiceMocked.confirmMasterByEmail(masterEmail);
//
//                verify(userRepositoryMocked).save(master);
//                verify(mailSenderMocked).sendRegistrationEmail(masterEmail);
//                assertTrue(master.isActive());
//            }
//
//            @Test
//            void confirmMasterByEmail_throws_when_already_active() {
//                master.setActive(true);
//                when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));
//
//                ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
//                        () -> userServiceMocked.confirmMasterByEmail(masterEmail));
//
//                assertEquals("Master is already active: email@testMaster.de", e.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class FindMasterUserByEmail_Tests {
//            @Test
//            void findMasterUserByEmail_successfully() {
//                master.setActive(false);
//                when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));
//
//                User result = userServiceMocked.findMasterUserByEmail(masterEmail);
//
//                assertEquals(master, result);
//            }
//
//            @Test
//            void findMasterUserByEmail_throws_when_not_master() {
//                client.setActive(false);
//                when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.of(client));
//
//                ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
//                        () -> userServiceMocked.findMasterUserByEmail(clientEmail));
//
//                assertEquals("User is not a master: email@testClient.de", e.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class GetAllMasters_Tests {
//            @Test
//            void getAllMasters_successfully() {
//                List<User> masters = List.of(master);
//                when(userRepositoryMocked.findAllMastersWithDetails()).thenReturn(masters);
//                when(userMapperMocked.userDetailsToDto(master)).thenReturn(userDetailsDto);
//
//                List<UserDetailsDto> result = userServiceMocked.getAllMasters();
//
//                assertEquals(1, result.size());
//                verify(userMapperMocked).userDetailsToDto(master);
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class GetAllUsers_Tests {
//            @Test
//            void getAllUsers_successfully() {
//                List<User> users = List.of(client, master);
//                when(userRepositoryMocked.findAllWithDetails()).thenReturn(users);
//                when(userMapperMocked.userDetailsToDto(client)).thenReturn(userDetailsDto);
//                when(userMapperMocked.userDetailsToDto(master)).thenReturn(userDetailsDto1);
//
//                List<UserDetailsDto> result = userServiceMocked.getAllUsers();
//
//                assertEquals(2, result.size());
//                verify(userMapperMocked, times(2)).userDetailsToDto(any());
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class FindUsersByCategoryId_Tests {
//            @Test
//            void findUsersByCategoryId_successfully() {
//                List<User> users = List.of(master);
//                when(userRepositoryMocked.findUsersByCategoryIdWithDetails(1L)).thenReturn(users);
//                when(userMapperMocked.userDetailsToDto(master)).thenReturn(userDetailsDto);
//
//                List<UserDetailsDto> result = userServiceMocked.findUsersByCategoryId(1L);
//
//                assertEquals(1, result.size());
//                verify(userMapperMocked).userDetailsToDto(master);
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class FindByEmail_Tests {
//            @Test
//            void findByEmail_successfully() {
//                when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.of(client));
//
//                Optional<User> result = userServiceMocked.findByEmail(clientEmail);
//
//                assertTrue(result.isPresent());
//                assertEquals(client, result.get());
//            }
//
//            @Test
//            void findByEmail_throws_when_email_empty() {
//                assertThrows(IllegalArgumentException.class,
//                        () -> userServiceMocked.findByEmail(""));
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(ReplaceUnderscores.class)
//        class DeleteById_Tests {
//            @Test
//            void deleteById_successfully() {
//                when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.of(client));
//
//                userServiceMocked.deleteById(clientId);
//
//                verify(userRepositoryMocked).delete(client);
//            }
//
//            @Test
//            void deleteById_throws_when_user_not_found() {
//                when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.empty());
//
//                ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
//                        () -> userServiceMocked.deleteById(clientId));
//
//                assertEquals("User not found for id: " + clientId, e.getMessage());
//            }
//        }
//    }
//}